import React, { useEffect, useState } from 'react';
import { getProductDetailForEdit, getCategories, updateProduct, uploadProductImage, deleteProductImage } from '../../api/api';

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
  status?: string; // '1': bán, '0': không bán
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
        const prod = res.data.result;
        prod.productVariants = prod.productVariants.map((v: any) => ({
          ...v,
          enabled: v.status === '1'
        }));
        setProduct(prod);
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
  const handleRemoveImage = async (id: string) => {
    if (!product) return;
    const img = product.images.find(img => img.id === id);
    if (!img) return;
    try {
      await deleteProductImage(img.url); // Gọi API xóa ảnh theo url
      setProduct({ ...product, images: product.images.filter(img => img.id !== id) });
    } catch (error) {
      alert('Xóa ảnh thất bại!');
    }
  };
  const handleSetMainImage = (id: string) => {
    if (!product) return;
    setProduct({
      ...product,
      images: product.images.map(img => ({ ...img, isMain: img.id === id }))
    });
  };

  // Handler cho thuộc tính (attributes)
  const createTempId = () => `temp_${Date.now()}_${Math.floor(Math.random() * 10000)}`;
  const handleAddAttribute = () => {};
  const handleRemoveAttribute = () => {};
  const handleAttributeNameChange = () => {};
  // Handler cho detailAttributes
  const handleAddDetailAttribute = () => {};
  const handleRemoveDetailAttribute = () => {};
  const handleDetailAttributeNameChange = () => {};

  // Handler cho variants (chỉ demo chỉnh sửa giá, stock, bật/tắt)
  const handleVariantChange = (variantId: string, field: string, value: any) => {
    if (!product) return;
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
            id: createTempId(),
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

  const isRealId = (id: string) => {
    if (!id) return false;
    return id.length > 10 && !id.startsWith('temp_');
  };

  const buildProductToSend = (product: Product) => {
    const newVariants = product.productVariants
      .filter(variant => variant.enabled !== false)
      .map(variant => ({
        id: isRealId(variant.id) ? variant.id : undefined,
        price: variant.price,
        stock: variant.stock,
        status: variant.status ?? (variant.enabled ? '1' : '0'),
        detailAttributes: variant.detailAttributes.map(detail => ({
          id: isRealId(detail.id) ? detail.id : undefined
        }))
      }));
    return {
      id: product.id,
      name: product.name,
      price: product.price,
      description: product.description,
      category: product.category ? { id: product.category.id } : null,
      images: product.images,
      productVariants: newVariants
    };
  };

  const handleSave = async () => {
    if (!product) return;
    setIsSaving(true);
    try {
      const productToSend = buildProductToSend(product);
      const response = await updateProduct(productId, productToSend);
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

  const handleChangeImage = async (id: string, file: File) => {
    if (!product) return;
    try {
      const res = await uploadProductImage(file);
      const url = res.data.url || res.data.result || res.data;
      setProduct({
        ...product,
        images: product.images.map(img => img.id === id ? { ...img, url } : img)
      });
    } catch (error) {
      alert('Upload ảnh thất bại!');
    }
  };

  const handleAddImages = async (files: FileList) => {
    if (!product) return;
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

  if (loading || !product || catLoading) {
    return <div className="text-center text-gray-500 py-16">Đang tải thông tin sản phẩm...</div>;
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
                <button type="button"
                  onClick={() => handleRemoveImage(img.id)}
                  disabled={product.images.length === 1}
                  className={`absolute -top-2 -right-2 bg-white border border-gray-300 rounded-full p-1 text-xs shadow hover:bg-red-100 ${product.images.length === 1 ? 'opacity-50 cursor-not-allowed' : ''}`}
                  title={product.images.length === 1 ? 'Phải có ít nhất 1 ảnh' : 'Xóa ảnh'}
                >✕</button>
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
                if (e.target.files && e.target.files.length > 0) {
                  handleAddImages(e.target.files);
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
        </div>
        <div className="space-y-4">
          {product.productAttributes.map(attr => (
            <div key={attr.id} className="bg-white border rounded-lg p-4 shadow-sm">
              <div className="font-semibold text-[#cc3333] mb-2 text-base flex items-center gap-2">
                <svg className="w-4 h-4 text-[#cc3333]" fill="none" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" stroke="#cc3333" strokeWidth="2"/></svg>
                {attr.name}
              </div>
              <ul className="list-disc list-inside pl-4 text-gray-700">
                {attr.detailAttributes.map(detail => (
                  <li key={detail.id} className="py-1">{detail.name}</li>
                ))}
              </ul>
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
                    <input type="checkbox" checked={variant.status === '1'} onChange={e => handleVariantChange(variant.id, 'enabled', e.target.checked)} />
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