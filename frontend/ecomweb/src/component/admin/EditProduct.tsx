import React, { useEffect, useState } from 'react';
import { getProductDetailForEdit, getCategories, updateProduct } from '../../api/api';

// Các interface mở rộng theo dữ liệu thực tế
interface ProductImage {
  id: string;
  url: string;
  isMain: boolean;
}
interface DetailAttribute {
  id: string;
  name: string;
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
}
interface Category {
  id: string;
  name: string;
}
interface Product {
  id: string;
  name: string;
  price: number;
  description: string;
  category: Category | null;
  images: ProductImage[];
  productAttributes: ProductAttribute[];
  productVariants: ProductVariant[];
}

interface EditProductProps {
  productId: string;
  onSaveSuccess: () => void;
  onCancel: () => void;
}

const EditProduct: React.FC<EditProductProps> = ({ productId, onSaveSuccess, onCancel }) => {
  const [product, setProduct] = useState<Product | null>(null);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [catLoading, setCatLoading] = useState(true);

  // Fetch product detail
  useEffect(() => {
    setLoading(true);
    getProductDetailForEdit(productId)
      .then(res => {
        setProduct(res.data.result);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [productId]);

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
    if (!product) return;
    setProduct({ ...product, [e.target.name]: e.target.value });
  };

  // Handler cho category
  const handleCategoryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    if (!product) return;
    const cat = categories.find(c => c.id === e.target.value);
    setProduct({ ...product, category: cat || null });
  };

  // Handler cho ảnh (chỉ demo, chưa upload thực)
  const handleAddImage = () => {
    if (!product) return;
    setProduct({
      ...product,
      images: [...product.images, { id: Date.now().toString(), url: '/vite.svg', isMain: false }]
    });
  };
  const handleRemoveImage = (id: string) => {
    if (!product) return;
    setProduct({ ...product, images: product.images.filter(img => img.id !== id) });
  };
  const handleSetMainImage = (id: string) => {
    if (!product) return;
    setProduct({
      ...product,
      images: product.images.map(img => ({ ...img, isMain: img.id === id }))
    });
  };

  // Handler cho thuộc tính (attributes)
  const handleAddAttribute = () => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: [...product.productAttributes, { id: Date.now().toString(), name: '', detailAttributes: [] }]
    });
  };
  const handleRemoveAttribute = (attrId: string) => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: product.productAttributes.filter(attr => attr.id !== attrId)
    });
  };
  const handleAttributeNameChange = (attrId: string, value: string) => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attrId ? { ...attr, name: value } : attr
      )
    });
  };
  // Handler cho detailAttributes
  const handleAddDetailAttribute = (attrId: string) => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attrId
          ? { ...attr, detailAttributes: [...attr.detailAttributes, { id: Date.now().toString(), name: '' }] }
          : attr
      )
    });
  };
  const handleRemoveDetailAttribute = (attrId: string, detailId: string) => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attrId
          ? { ...attr, detailAttributes: attr.detailAttributes.filter(d => d.id !== detailId) }
          : attr
      )
    });
  };
  const handleDetailAttributeNameChange = (attrId: string, detailId: string, value: string) => {
    if (!product) return;
    setProduct({
      ...product,
      productAttributes: product.productAttributes.map(attr =>
        attr.id === attrId
          ? {
              ...attr,
              detailAttributes: attr.detailAttributes.map(d =>
                d.id === detailId ? { ...d, name: value } : d
              )
            }
          : attr
      )
    });
  };

  // Handler cho variants (chỉ demo chỉnh sửa giá, stock, bật/tắt)
  const handleVariantChange = (variantId: string, field: string, value: any) => {
    if (!product) return;
    setProduct({
      ...product,
      productVariants: product.productVariants.map(variant =>
        variant.id === variantId ? { ...variant, [field]: value } : variant
      )
    });
  };

  // Handler tạo lại variants từ attributes (giữ lại giá trị cũ nếu trùng tổ hợp)
  const handleGenerateVariants = () => {
    if (!product) return;
    // Lấy tất cả các detailAttributes của từng attribute
    const attrDetails = product.productAttributes.map(attr => attr.detailAttributes);
    // Sinh tổ hợp
    const combine = (arr: DetailAttribute[][]): DetailAttribute[][] => {
      if (arr.length === 0) return [[]];
      const rest = combine(arr.slice(1));
      return arr[0].flatMap(d => rest.map(r => [d, ...r]));
    };
    const combos = combine(attrDetails).filter(c => c.length === product.productAttributes.length);

    // Hàm so sánh tổ hợp detailAttributes
    const isSameCombo = (a: DetailAttribute[], b: DetailAttribute[]) =>
      a.length === b.length && a.every((d, i) => d.id === b[i].id);

    // Tạo variants mới, giữ lại giá trị cũ nếu trùng tổ hợp
    const newVariants = combos.map((combo, idx) => {
      const old = product.productVariants.find(
        v => isSameCombo(v.detailAttributes, combo)
      );
      return old
        ? { ...old }
        : {
            id: Date.now().toString() + idx,
            price: product.price,
            stock: 0,
            detailAttributes: combo,
            enabled: false
          };
    });

    setProduct({
      ...product,
      productVariants: newVariants
    });
  };

  const handleSave = async () => {
    if (!product) return;
    setIsSaving(true);
    try {
      const response = await updateProduct(productId, product);
      if (response.status === 200) {
        alert('Cập nhật sản phẩm thành công!');
        onSaveSuccess();
      } else {
        const errorData = response.data;
        alert(`Lỗi khi cập nhật: ${errorData.message || 'Vui lòng thử lại.'}`);
      }
    } catch (error) {
      console.error('Lỗi khi lưu sản phẩm:', error);
      alert('Đã có lỗi xảy ra. Không thể lưu sản phẩm.');
    } finally {
      setIsSaving(false);
    }
  };

  const handleChangeImage = (id: string, file: File) => {
    if (!product) return;
    const url = URL.createObjectURL(file); // preview, thực tế nên upload lên server lấy url thực
    setProduct({
      ...product,
      images: product.images.map(img => img.id === id ? { ...img, url } : img)
    });
  };

  if (loading || !product || catLoading) {
    return <div className="text-center text-gray-500 py-16">Đang tải thông tin sản phẩm...</div>;
  }

  return (
    <div className="max-w-3xl mx-auto bg-white rounded-lg shadow p-8 mt-8">
      <h2 className="text-2xl font-bold mb-6 text-[#cc3333]">Chỉnh sửa sản phẩm</h2>
      {/* Thông tin cơ bản */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div>
          <label className="block text-sm font-medium mb-1">Tên sản phẩm</label>
          <input type="text" name="name" value={product.name} onChange={handleBasicChange} className="w-full border rounded px-3 py-2 mb-2" />
          <label className="block text-sm font-medium mb-1">Giá mặc định</label>
          <input type="number" name="price" value={product.price} onChange={handleBasicChange} className="w-full border rounded px-3 py-2 mb-2" />
          <label className="block text-sm font-medium mb-1">Danh mục</label>
          <select name="category" value={product.category?.id || ''} onChange={handleCategoryChange} className="w-full border rounded px-3 py-2 mb-2">
            <option value="">Chọn danh mục</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
          <label className="block text-sm font-medium mb-1">Mô tả</label>
          <textarea name="description" value={product.description} onChange={handleBasicChange} className="w-full border rounded px-3 py-2 mb-2" />
        </div>
        <div>
          <label className="block text-sm font-medium mb-1">Ảnh sản phẩm</label>
          <div className="flex flex-wrap gap-2 mb-2">
            {product.images.map(img => (
              <div key={img.id} className="relative group">
                <input
                  type="file"
                  accept="image/*"
                  style={{ display: 'none' }}
                  id={`file-input-${img.id}`}
                  onChange={e => {
                    if (e.target.files && e.target.files[0]) {
                      handleChangeImage(img.id, e.target.files[0]);
                    }
                  }}
                />
                <img
                  src={img.url}
                  alt="img"
                  className={`h-20 w-20 object-cover rounded border-2 ${img.isMain ? 'border-[#cc3333]' : 'border-gray-200'} cursor-pointer`}
                  onClick={() => document.getElementById(`file-input-${img.id}`)?.click()}
                />
                <button type="button" onClick={() => handleRemoveImage(img.id)} className="absolute -top-2 -right-2 bg-white border border-gray-300 rounded-full p-1 text-xs shadow hover:bg-red-100">✕</button>
                {!img.isMain && (
                  <button type="button" onClick={() => handleSetMainImage(img.id)} className="absolute bottom-1 left-1 bg-white border border-gray-300 rounded px-1 text-xs shadow hover:bg-gray-100">Chọn chính</button>
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
                if (e.target.files && e.target.files.length > 0 && product) {
                  const files = Array.from(e.target.files);
                  const newImages = files.map(file => ({
                    id: Date.now().toString() + file.name,
                    url: URL.createObjectURL(file),
                    isMain: false,
                  }));

                  const updatedImages = [...product.images, ...newImages];
                  if (updatedImages.length > 0 && !updatedImages.some(img => img.isMain)) {
                    updatedImages[0].isMain = true;
                  }
                  
                  setProduct({
                    ...product,
                    images: updatedImages,
                  });
                  e.target.value = ''; 
                }
              }}
            />
            <button type="button" onClick={() => document.getElementById('add-new-image-input')?.click()} className="h-20 w-20 flex items-center justify-center border-2 border-dashed rounded text-gray-400 hover:border-[#cc3333]">+</button>
          </div>
        </div>
      </div>
      {/* Thuộc tính sản phẩm */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-2">
          <h3 className="text-lg font-semibold">Thuộc tính sản phẩm</h3>
          <button type="button" onClick={handleAddAttribute} className="px-3 py-1 rounded bg-[#cc3333] text-white text-sm">+ Thêm thuộc tính</button>
        </div>
        <div className="space-y-4">
          {product.productAttributes.map(attr => (
            <div key={attr.id} className="bg-gray-50 rounded p-3">
              <div className="flex items-center gap-2 mb-2">
                <input type="text" value={attr.name} onChange={e => handleAttributeNameChange(attr.id, e.target.value)} className="border rounded px-2 py-1 w-40" placeholder="Tên thuộc tính" />
                <button type="button" onClick={() => handleRemoveAttribute(attr.id)} className="text-red-500 text-xs">Xóa</button>
              </div>
              <div className="flex flex-wrap gap-2">
                {attr.detailAttributes.map(detail => (
                  <div key={detail.id} className="flex items-center gap-1 bg-white border rounded px-2 py-1">
                    <input type="text" value={detail.name} onChange={e => handleDetailAttributeNameChange(attr.id, detail.id, e.target.value)} className="w-20" placeholder="Giá trị" />
                    <button type="button" onClick={() => handleRemoveDetailAttribute(attr.id, detail.id)} className="text-red-400 text-xs">✕</button>
                  </div>
                ))}
                <button type="button" onClick={() => handleAddDetailAttribute(attr.id)} className="text-[#cc3333] text-xs px-2 py-1 border border-dashed rounded">+ Giá trị</button>
              </div>
            </div>
          ))}
        </div>
      </div>
      {/* Biến thể sản phẩm */}
      <div className="mb-8">
        <div className="flex items-center justify-between mb-2">
          <h3 className="text-lg font-semibold">Biến thể sản phẩm</h3>
          <button type="button" onClick={handleGenerateVariants} className="px-3 py-1 rounded bg-[#cc3333] text-white text-sm">Tạo lại biến thể từ thuộc tính</button>
        </div>
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
                    <input type="number" value={variant.price} min={0} onChange={e => handleVariantChange(variant.id, 'price', Number(e.target.value))} className="w-20 border rounded px-2 py-1" />
                  </td>
                  <td className="py-2 px-3 text-center">
                    <input type="number" value={variant.stock} min={0} onChange={e => handleVariantChange(variant.id, 'stock', Number(e.target.value))} className="w-16 border rounded px-2 py-1" />
                  </td>
                  <td className="py-2 px-3 text-center">
                    <input type="checkbox" checked={variant.enabled !== false} onChange={e => handleVariantChange(variant.id, 'enabled', e.target.checked)} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      <div className="flex justify-end gap-2 mt-6">
        <button type="button" onClick={onCancel} className="px-4 py-2 rounded bg-gray-100 hover:bg-gray-200 text-gray-700">Hủy</button>
        <button 
          type="button" 
          onClick={handleSave} 
          disabled={isSaving}
          className="px-4 py-2 rounded bg-[#cc3333] text-white hover:bg-[#b82d2d] font-semibold disabled:bg-gray-400"
        >
          {isSaving ? 'Đang lưu...' : 'Lưu thay đổi'}
        </button>
      </div>
    </div>
  );
};

export default EditProduct; 