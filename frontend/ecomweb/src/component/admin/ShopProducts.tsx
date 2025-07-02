import React, { useEffect, useRef, useState } from 'react';
import { getProductsByUserId } from '../../api/api';

export interface ProductImage {
  id: string;
  url: string;
  isMain: boolean;
}

export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  status: string | number;
  createdAt: string;
  category: any;
  shop: any;
  images: ProductImage[];
  productVariants: any;
  productAttributes: any;
}

interface ShopProductsProps {
  userId: String;
  onEdit: (productId: string) => void;
}

const itemsPerPage = 10;

const ShopProducts: React.FC<ShopProductsProps> = ({ userId, onEdit }) => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState(searchTerm);
  const [statusFilter, setStatusFilter] = useState<'all' | 'active' | 'inactive'>('all');
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Ref cho input
  const inputRef = useRef<HTMLInputElement>(null);

  // Debounce searchTerm
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearch(searchTerm);
    }, 400);
    return () => clearTimeout(handler);
  }, [searchTerm]);

  // Reset page về 1 khi filter/search thay đổi
  useEffect(() => {
    setCurrentPage(1);
  }, [debouncedSearch, statusFilter]);

  useEffect(() => {
    setLoading(true);
    getProductsByUserId(userId as string, currentPage - 1, itemsPerPage, debouncedSearch, statusFilter)
      .then((res) => {
        setProducts(res.data.result.content);
        setTotalPages(res.data.result.totalPages);
        setTotalElements(res.data.result.totalElements);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [userId, currentPage, debouncedSearch, statusFilter]);

  // Focus lại input sau khi loading xong
  useEffect(() => {
    if (!loading && inputRef.current) {
      inputRef.current.focus();
    }
  }, [loading]);

  if (loading) {
    return <div className="text-center text-gray-500 py-16">Đang tải danh sách sản phẩm...</div>;
  }

  return (
    <div className="bg-white rounded-xl shadow-md p-4">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-2 mb-4">
        <h2 className="text-2xl font-bold text-[#cc3333] flex items-center gap-2">
          <svg className="w-7 h-7 text-[#cc3333]" fill="none" viewBox="0 0 24 24"><path d="M3 7V6a3 3 0 0 1 3-3h12a3 3 0 0 1 3 3v1" stroke="#cc3333" strokeWidth="2" strokeLinecap="round"/><rect x="3" y="7" width="18" height="14" rx="2" stroke="#cc3333" strokeWidth="2"/></svg>
          Quản lý sản phẩm
        </h2>
        <div className="flex flex-col md:flex-row gap-2 w-full md:w-auto">
          <input
            ref={inputRef}
            type="text"
            placeholder="Tìm kiếm sản phẩm..."
            className="border rounded px-3 py-2 w-full md:w-56 focus:outline-[#cc3333]"
            value={searchTerm}
            onChange={e => setSearchTerm(e.target.value)}
          />
          <select
            className="border rounded px-2 py-2 focus:outline-[#cc3333]"
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value as any)}
          >
            <option value="all">Tất cả trạng thái</option>
            <option value="active">Đang bán</option>
            <option value="inactive">Ẩn</option>
          </select>
        </div>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full text-sm rounded-xl">
          <thead>
            <tr className="bg-[#faeaea] text-[#cc3333]">
              <th className="py-3 px-3 text-center rounded-tl-xl">
                <input type="checkbox" className="accent-[#cc3333] w-4 h-4 rounded" />
              </th>
              <th className="py-3 px-3 text-center">STT</th>
              <th className="py-3 px-3 text-center">Ảnh</th>
              <th className="py-3 px-3 text-center">Tên sản phẩm</th>
              <th className="py-3 px-3 text-center">Giá</th>
              <th className="py-3 px-3 text-center">Ngày tạo</th>
              <th className="py-3 px-3 text-center">Trạng thái</th>
              <th className="py-3 px-3 text-center rounded-tr-xl">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {products.length === 0 ? (
              <tr>
                <td colSpan={8} className="py-8 text-center text-gray-400">Không có sản phẩm nào phù hợp.</td>
              </tr>
            ) : products.map((p, idx) => {
              const mainImage = p.images?.find(img => img.isMain);
              return (
                <tr
                  key={p.id}
                  className="hover:bg-[#fff3f3] transition-all duration-200 border-b last:border-b-0"
                >
                  <td className="py-2 px-3 text-center">
                    <input type="checkbox" className="accent-[#cc3333] w-4 h-4 rounded" />
                  </td>
                  <td className="py-2 px-3 text-center font-semibold text-gray-500">{(currentPage - 1) * itemsPerPage + idx + 1}</td>
                  <td className="py-2 px-3 text-center">
                    {mainImage ? (
                      <img
                        src={mainImage.url}
                        alt={p.name}
                        className="h-12 w-12 object-cover rounded-full border-2 border-[#faeaea] mx-auto shadow-sm"
                      />
                    ) : (
                      <span className="text-gray-400">No image</span>
                    )}
                  </td>
                  <td className="py-2 px-3 text-center font-medium">{p.name}</td>
                  <td className="py-2 px-3 text-center font-semibold text-[#cc3333]">{p.price.toLocaleString()} đ</td>
                  <td className="py-2 px-3 text-center text-gray-500">
                    {new Date(p.createdAt).toLocaleDateString('vi-VN')}
                  </td>
                  <td className="py-2 px-3 text-center">
                    <span className={`inline-block px-3 py-1 rounded-full text-xs font-bold
                      ${Number(p.status) === 1
                        ? 'bg-green-100 text-green-700 border border-green-200'
                        : 'bg-gray-200 text-gray-500 border border-gray-300'}`}>
                      {Number(p.status) === 1 ? 'Đang bán' : 'Ẩn'}
                    </span>
                  </td>
                  <td className="py-2 px-3 text-center">
                    <button
                      className="inline-flex items-center justify-center w-8 h-8 rounded hover:bg-blue-50 text-blue-600 transition"
                      title="Chỉnh sửa"
                      onClick={() => onEdit(p.id)}
                    >
                      <svg width="18" height="18" fill="none" viewBox="0 0 24 24">
                        <path d="M4 21h4.586a1 1 0 0 0 .707-.293l10.5-10.5a2 2 0 0 0 0-2.828l-2.172-2.172a2 2 0 0 0-2.828 0l-10.5 10.5A1 1 0 0 0 3 19.414V21a1 1 0 0 0 1 1z"
                          stroke="#2563eb" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                        <path d="M14.5 6.5l3 3" stroke="#2563eb" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
                      </svg>
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center mt-4 gap-2">
          <button
            disabled={currentPage === 1}
            onClick={() => setCurrentPage(p => p - 1)}
            className="px-3 py-1 rounded border bg-white hover:bg-gray-100 disabled:opacity-50"
          >Trước</button>
          {[...Array(totalPages)].map((_, i) => (
            <button
              key={i}
              onClick={() => setCurrentPage(i + 1)}
              className={`px-3 py-1 rounded border ${currentPage === i + 1 ? 'bg-[#cc3333] text-white' : 'bg-white hover:bg-gray-100'}`}
            >{i + 1}</button>
          ))}
          <button
            disabled={currentPage === totalPages}
            onClick={() => setCurrentPage(p => p + 1)}
            className="px-3 py-1 rounded border bg-white hover:bg-gray-100 disabled:opacity-50"
          >Sau</button>
        </div>
      )}
    </div>
  );
}

export default ShopProducts; 