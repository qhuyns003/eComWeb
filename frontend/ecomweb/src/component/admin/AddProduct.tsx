import React, { useEffect, useState } from 'react';
import { getCategories, createProduct, uploadProductImage } from '../../api/api';

// Các interface mở rộng theo dữ liệu thực tế
interface ProductImage {
  id: string;
  url: string;
  isMain: boolean;
}
interface DetailAttribute {
  id: string;
  name: string;
  productAttribute?: { name: string };
}
interface ProductAttribute {
  id: string;
  name: string;
  detailAttributes: DetailAttribute[];
}
interface ProductVariant {
  id: string;
  price: number;
  stock: number;
  detailAttributes: DetailAttribute[];
  image?: ProductImage;
  enabled?: boolean;
  status?: string; // '1': bán, '0': không bán
}
interface Category {
  id: string;
  name: string;
}
interface Product {
  name: string;
  price: number;
  description: string;
  category: Category | null;
  images: ProductImage[];
  productAttributes: ProductAttribute[];
  productVariants: ProductVariant[];
  weight: string;
  length: string;
  width: string;
  height: string;
}

interface AddProductProps {
  onSaveSuccess: () => void;
  onCancel: () => void;
}

const AddProduct: React.FC<AddProductProps> = ({ onSaveSuccess, onCancel }) => {
  const [product, setProduct] = useState<Product>({
    name: '',
    price: 0,
    description: '',
    category: null,
    images: [],
    productAttributes: [],
    productVariants: [],
    weight: "",
    length: "",
    width: "",
    height: ""
  });
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [catLoading, setCatLoading] = useState(true);

  // Fetch categories
  useEffect(() => {
    setCatLoading(true);
    getCategories()
      .then(res => {
        setCategories(res.data.result || res.data);
        setCatLoading(false);
      })
      .catch(() => setCatLoading(false));
  }, []);

  // Handler cho các trường cơ bản
  const handleBasicChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setProduct({ ...product, [e.target.name]: e.target.value });
  };

  // Handler cho category
  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const cat = categories.find(c => c.id === e.target.value);
    setProduct({ ...product, category: cat || null });
  };

  // Handler cho ảnh
  const handleRemoveImage = async (id: string) => {
    setProduct({ ...product, images: product.images.filter(img => img.id !== id) });
  };

  const handleSetMainImage = (id: string) => {
    setProduct({
      ...product,
      images: product.images.map(img => ({ ...img, isMain: img.id === id }))
    });
  };

  // Handler cho thuộc tính (attributes)
  const createTempId = () => `temp_${Date.now()}_${Math.floor(Math.random() * 10000)}`;

  const handleAddAttribute = () => {
    const newAttribute: ProductAttribute = {
      id: createTempId(),
      name: '',
      detailAttributes: []
    };
    setProduct({
      ...product,
      productAttributes: [...product.productAttributes, newAttribute]
    });
  };

  const handleRemoveAttribute = (attributeId: string) => {
    setProduct({
      ...product,
      productAttributes: product.productAttributes.filter(attr => attr.id !== attributeId)
    });
  };

  const handleAttributeNameChange = (attributeId: string, name: string) => {
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attributeId ? { ...attr, name } : attr
      )
    });
  };

  // Handler cho detailAttributes
  const handleAddDetailAttribute = (attributeId: string) => {
    const newDetail: DetailAttribute = {
      id: createTempId(),
      name: ''
    };
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attributeId
          ? { ...attr, detailAttributes: [...attr.detailAttributes, newDetail] }
          : attr
      )
    });
  };

  const handleRemoveDetailAttribute = (attributeId: string, detailId: string) => {
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attributeId
          ? { ...attr, detailAttributes: attr.detailAttributes.filter(d => d.id !== detailId) }
          : attr
      )
    });
  };

  const handleDetailAttributeNameChange = (attributeId: string, detailId: string, name: string) => {
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attributeId
          ? {
              ...attr,
              detailAttributes: attr.detailAttributes.map(d =>
                d.id === detailId ? { ...d, name } : d
              )
            }
          : attr
      )
    });
  };

  // Handler cho variants
  const handleVariantChange = (variantId: string, field: string, value: any) => {
    setProduct({
      ...product,
      productVariants: product.productVariants.map(variant => {
        if (variant.id === variantId) {
          if (field === 'enabled') {
            return {
              ...variant,
              enabled: value,
              status: value ? '1' : '0'
            };
          }
          return { ...variant, [field]: value };
        }
        return variant;
      })
    });
  };

  // Handler tạo lại variants từ attributes
  const handleGenerateVariants = () => {
    if (product.productAttributes.length === 0) {
      alert('Vui lòng thêm thuộc tính sản phẩm trước!');
      return;
    }

    // Lấy tất cả các detailAttributes của từng attribute
    const attrDetails = product.productAttributes.map(attr => attr.detailAttributes);
    
    // Kiểm tra xem có attribute nào chưa có detailAttributes không
    if (attrDetails.some(details => details.length === 0)) {
      alert('Vui lòng thêm giá trị cho tất cả thuộc tính!');
      return;
    }

    // Sinh tổ hợp
    const combine = (arr: DetailAttribute[][]): DetailAttribute[][] => {
      if (arr.length === 0) return [[]];
      const rest = combine(arr.slice(1));
      return arr[0].flatMap(d => rest.map(r => [d, ...r]));
    };
    const combos = combine(attrDetails).filter(c => c.length === product.productAttributes.length);

    // Tạo variants mới
    const newVariants = combos.map((combo, idx) => ({
      id: createTempId(),
      price: product.price,
      stock: 0,
      detailAttributes: combo,
      enabled: false
    }));

    setProduct({
      ...product,
      productVariants: newVariants
    });
  };

  const buildProductToSend = (product: Product) => {
    const newVariants = product.productVariants
      .filter(variant => variant.enabled !== false)
      .map(variant => ({
        price: variant.price,
        stock: variant.stock,
        status: variant.status ?? (variant.enabled ? '1' : '0'),
        detailAttributes: variant.detailAttributes.map((detail, idx) => {
          // Lấy productAttribute cha theo index (vì FE đang giữ đúng thứ tự)
          const attr = product.productAttributes[idx];
          return {
            name: detail.name,
            productAttribute: attr ? { name: attr.name } : undefined
          };
        })
      }));

    const newAttributes = product.productAttributes.map(attr => ({
      name: attr.name,
      detailAttributes: attr.detailAttributes.map(detail => ({
        name: detail.name
      }))
    }));

    return {
      name: product.name,
      price: product.price,
      description: product.description,
      category: product.category ? { id: product.category.id } : null,
      images: product.images,
      productVariants: newVariants,
      productAttributes: newAttributes,
      weight: Number(product.weight) || 0,
      length: Number(product.length) || 0,
      width: Number(product.width) || 0,
      height: Number(product.height) || 0
    };
  };

  const handleSave = async () => {
    // Nếu chưa có thuộc tính nào, tự động thêm thuộc tính và variant mặc định
    if (product.productAttributes.length === 0) {
      const defaultAttrId = createTempId();
      const defaultDetailId = createTempId();
      const defaultAttribute = {
        id: defaultAttrId,
        name: "Loại",
        detailAttributes: [{ id: defaultDetailId, name: "Mặc định" }]
      };
      product.productAttributes = [defaultAttribute];
      product.productVariants = [{
        id: createTempId(),
        price: product.price,
        stock: 0,
        detailAttributes: [{
          id: defaultDetailId,
          name: "Mặc định",
          productAttribute: { name: "Loại" }
        }],
        enabled: true,
        status: "1"
      }];
      setProduct({ ...product });
    }

    if (!product.name || !product.category) {
      alert('Vui lòng điền đầy đủ thông tin bắt buộc!');
      return;
    }

    if (product.images.length === 0) {
      alert('Vui lòng thêm ít nhất một ảnh sản phẩm!');
      return;
    }

    setIsSaving(true);
    try {
      const productToSend = buildProductToSend(product);
      const response = await createProduct(productToSend);
      if (response.status === 200 || response.status === 201) {
        alert('Thêm sản phẩm thành công!');
        onSaveSuccess();
      } else {
        const errorData = response.data;
        alert(`Lỗi khi thêm sản phẩm: ${errorData.message || 'Vui lòng thử lại.'}`);
      }
    } catch (error) {
      console.error('Lỗi khi thêm sản phẩm:', error);
      alert('Đã có lỗi xảy ra. Không thể thêm sản phẩm.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleAddImages = async (files: FileList) => {
    const fileArr = Array.from(files);
    const uploadedImages: ProductImage[] = [];
    
    for (const file of fileArr) {
      try {
        const res = await uploadProductImage(file);
        const url = res.data.url || res.data.result || res.data;
        uploadedImages.push({
          id: Date.now().toString() + file.name,
          url,
          isMain: false,
        });
      } catch {
        alert('Upload ảnh thất bại!');
      }
    }
    
    const updatedImages = [...product.images, ...uploadedImages];
    if (updatedImages.length > 0 && !updatedImages.some(img => img.isMain)) {
      updatedImages[0].isMain = true;
    }
    
    setProduct({
      ...product,
      images: updatedImages,
    });
  };

  if (catLoading) {
    return <div className="text-center text-gray-500 py-16">Đang tải danh mục...</div>;
  }

  return (
    <div className="max-w-3xl mx-auto bg-white rounded-lg shadow p-8 mt-8 relative">
      <button
        type="button"
        onClick={onCancel}
        className="absolute top-4 right-4 px-3 py-1 rounded bg-gray-200 hover:bg-gray-300 text-gray-700 flex items-center gap-1 z-10"
      >
        <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24"><path d="M15 19l-7-7 7-7" stroke="#333" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/></svg>
        Quay lại
      </button>
      <h2 className="text-2xl font-bold mb-6 text-[#cc3333]">Thêm sản phẩm mới</h2>
      
      {/* Thông tin cơ bản */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div>
          <label className="block text-sm font-medium mb-1">Tên sản phẩm *</label>
          <input 
            type="text" 
            name="name" 
            value={product.name} 
            onChange={handleBasicChange} 
            className="w-full border rounded px-3 py-2 mb-2" 
            placeholder="Nhập tên sản phẩm"
          />
          <label className="block text-sm font-medium mb-1">Giá mặc định *</label>
          <input 
            type="number" 
            name="price" 
            value={product.price} 
            onChange={handleBasicChange} 
            className="w-full border rounded px-3 py-2 mb-2" 
            placeholder="0"
            min="0"
          />
          <label className="block text-sm font-medium mb-1">Danh mục *</label>
          <select 
            name="category" 
            value={product.category?.id || ''} 
            onChange={handleCategoryChange} 
            className="w-full border rounded px-3 py-2 mb-2"
          >
            <option value="">Chọn danh mục</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          <label className="block text-sm font-medium mb-1">Mô tả</label>
          <textarea 
            name="description" 
            value={product.description} 
            onChange={handleBasicChange} 
            className="w-full border rounded px-3 py-2 mb-2" 
            rows={4}
            placeholder="Mô tả sản phẩm..."
          />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Ảnh sản phẩm *</label>
          <div className="flex flex-wrap gap-2 mb-2">
            {product.images.map(img => (
              <div key={img.id} className="relative group">
                <img
                  src={img.url}
                  alt="img"
                  className={`h-20 w-20 object-cover rounded border-2 ${img.isMain ? 'border-[#cc3333]' : 'border-gray-200'}`}
                />
                <button 
                  type="button" 
                  onClick={() => handleRemoveImage(img.id)} 
                  className="absolute -top-2 -right-2 bg-white border border-gray-300 rounded-full p-1 text-xs shadow hover:bg-red-100"
                >
                  ✕
                </button>
                {!img.isMain && (
                  <button 
                    type="button" 
                    onClick={() => handleSetMainImage(img.id)} 
                    className="absolute bottom-1 left-1 bg-white border border-gray-300 rounded px-1 text-xs shadow hover:bg-gray-100"
                  >
                    Chọn chính
                  </button>
                )}
                {img.isMain && <span className="absolute bottom-1 left-1 bg-[#cc3333] text-white text-xs rounded px-1">Chính</span>}
              </div>
            ))}
            <input
              type="file"
              accept="image/*"
              multiple
              style={{ display: 'none' }}
              id="add-new-image-input"
              onChange={e => {
                if (e.target.files && e.target.files.length > 0) {
                  handleAddImages(e.target.files);
                  e.target.value = '';
                }
              }}
            />
            <button 
              type="button" 
              onClick={() => document.getElementById('add-new-image-input')?.click()} 
              className="h-20 w-20 flex items-center justify-center border-2 border-dashed rounded text-gray-400 hover:border-[#cc3333]"
            >
              +
            </button>
          </div>
        </div>
        {/* Trọng lượng & Kích thước */}
        <div className="col-span-2 mb-8">
          <div className="font-bold text-base text-black mb-2">Trọng lượng & Kích thước</div>
          <div className="text-xs text-red-500 mb-3">Nên nhập kích thước và trọng lượng ứng với biến thể lớn nhất để tránh rủi ro đơn vị vận chuyển từ chối nhận hàng do sai sót thông tin.</div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold mb-1 text-gray-600">Trọng lượng (g)</label>
              <input
                type="number"
                name="weight"
                value={product.weight}
                onChange={handleBasicChange}
                min={0}
                step={0.01}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-4 py-2 focus:border-[#cc3333] focus:ring-2 focus:ring-[#cc3333] transition-all shadow-sm bg-white placeholder-gray-400 text-base"
                placeholder="VD: 500"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold mb-1 text-gray-600">Dài (cm)</label>
              <input
                type="number"
                name="length"
                value={product.length}
                onChange={handleBasicChange}
                min={0}
                step={0.01}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-4 py-2 focus:border-[#cc3333] focus:ring-2 focus:ring-[#cc3333] transition-all shadow-sm bg-white placeholder-gray-400 text-base"
                placeholder="VD: 20"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold mb-1 text-gray-600">Rộng (cm)</label>
              <input
                type="number"
                name="width"
                value={product.width}
                onChange={handleBasicChange}
                min={0}
                step={0.01}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-4 py-2 focus:border-[#cc3333] focus:ring-2 focus:ring-[#cc3333] transition-all shadow-sm bg-white placeholder-gray-400 text-base"
                placeholder="VD: 10"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold mb-1 text-gray-600">Cao (cm)</label>
              <input
                type="number"
                name="height"
                value={product.height}
                onChange={handleBasicChange}
                min={0}
                step={0.01}
                required
                className="w-full border-2 border-gray-200 rounded-xl px-4 py-2 focus:border-[#cc3333] focus:ring-2 focus:ring-[#cc3333] transition-all shadow-sm bg-white placeholder-gray-400 text-base"
                placeholder="VD: 5"
              />
            </div>
          </div>
        </div>
      </div>

      {/* Thuộc tính sản phẩm */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">Thuộc tính sản phẩm</h3>
          <button 
            type="button" 
            onClick={handleAddAttribute} 
            className="px-3 py-1 rounded bg-[#cc3333] text-white text-sm"
          >
            + Thêm thuộc tính
          </button>
        </div>
        <div className="space-y-4">
          {product.productAttributes.map(attr => (
            <div key={attr.id} className="bg-gray-50 border rounded-lg p-4">
              <div className="flex items-center gap-2 mb-3">
                <input
                  type="text"
                  value={attr.name}
                  onChange={(e) => handleAttributeNameChange(attr.id, e.target.value)}
                  placeholder="Tên thuộc tính (VD: Màu sắc, Kích thước)"
                  className="flex-1 border rounded px-3 py-2"
                />
                <button
                  type="button"
                  onClick={() => handleRemoveAttribute(attr.id)}
                  className="px-2 py-2 text-red-600 hover:bg-red-100 rounded"
                >
                  ✕
                </button>
              </div>
              <div className="ml-4">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-sm font-medium">Giá trị:</span>
                  <button
                    type="button"
                    onClick={() => handleAddDetailAttribute(attr.id)}
                    className="px-2 py-1 text-sm bg-blue-500 text-white rounded hover:bg-blue-600"
                  >
                    + Thêm giá trị
                  </button>
                </div>
                <div className="space-y-2">
                  {attr.detailAttributes.map(detail => (
                    <div key={detail.id} className="flex items-center gap-2">
                      <input
                        type="text"
                        value={detail.name}
                        onChange={(e) => handleDetailAttributeNameChange(attr.id, detail.id, e.target.value)}
                        placeholder="Giá trị (VD: Đỏ, Xanh)"
                        className="flex-1 border rounded px-2 py-1 text-sm"
                      />
                      <button
                        type="button"
                        onClick={() => handleRemoveDetailAttribute(attr.id, detail.id)}
                        className="px-2 py-1 text-red-600 hover:bg-red-100 rounded text-sm"
                      >
                        ✕
                      </button>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Biến thể sản phẩm */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold">Biến thể sản phẩm</h3>
          <button 
            type="button" 
            onClick={handleGenerateVariants} 
            className="px-3 py-1 rounded bg-[#cc3333] text-white text-sm"
          >
            Tạo biến thể từ thuộc tính
          </button>
        </div>
        {product.productVariants.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm rounded-xl">
              <thead>
                <tr className="bg-[#faeaea] text-[#cc3333]">
                  {product.productAttributes.map(attr => (
                    <th key={attr.id} className="py-2 px-3 text-center">{attr.name}</th>
                  ))}
                  <th className="py-2 px-3 text-center">Giá</th>
                  <th className="py-2 px-3 text-center">Tồn kho</th>
                  <th className="py-2 px-3 text-center">Bán</th>
                </tr>
              </thead>
              <tbody>
                {product.productVariants.map(variant => (
                  <tr key={variant.id} className="hover:bg-[#fff3f3] transition-all duration-200 border-b last:border-b-0">
                    {product.productAttributes.map((attr, idx) => (
                      <td key={attr.id} className="py-2 px-3 text-center">
                        {variant.detailAttributes[idx]?.name || '-'}
                      </td>
                    ))}
                    <td className="py-2 px-3 text-center">
                      <input 
                        type="number" 
                        value={variant.price} 
                        min={0} 
                        onChange={e => handleVariantChange(variant.id, 'price', Number(e.target.value))} 
                        className="w-20 border rounded px-2 py-1" 
                      />
                    </td>
                    <td className="py-2 px-3 text-center">
                      <input 
                        type="number" 
                        value={variant.stock} 
                        min={0} 
                        onChange={e => handleVariantChange(variant.id, 'stock', Number(e.target.value))} 
                        className="w-16 border rounded px-2 py-1" 
                      />
                    </td>
                    <td className="py-2 px-3 text-center">
                      <input 
                        type="checkbox" 
                        checked={variant.status === '1'} 
                        onChange={e => handleVariantChange(variant.id, 'enabled', e.target.checked)} 
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-8 text-gray-500">
            Chưa có biến thể. Hãy thêm thuộc tính và nhấn "Tạo biến thể từ thuộc tính"
          </div>
        )}
      </div>

      <div className="flex justify-end gap-2 mt-6">
        <button 
          type="button" 
          onClick={onCancel} 
          className="px-4 py-2 rounded bg-gray-100 hover:bg-gray-200 text-gray-700"
        >
          Hủy
        </button>
        <button 
          type="button" 
          onClick={handleSave} 
          disabled={isSaving}
          className="px-4 py-2 rounded bg-[#cc3333] text-white hover:bg-[#b82d2d] font-semibold disabled:bg-gray-400"
        >
          {isSaving ? 'Đang lưu...' : 'Thêm sản phẩm'}
        </button>
      </div>
    </div>
  );
};

export default AddProduct; 