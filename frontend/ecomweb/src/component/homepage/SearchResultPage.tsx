import React, { useEffect, useState } from 'react';
import { FaFilter } from 'react-icons/fa';
import Header from '../layout/Header';
import Footer from '../layout/Footer';
import ProductCard from './ProductCard';
import { searchProduct, getCategories, getProvinces } from '../../api/api';
import { useLocation, useNavigate } from 'react-router-dom';

function useQuery() {
  return new URLSearchParams(useLocation().search);
}

const SearchResultPage: React.FC = () => {
  const query = useQuery();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [products, setProducts] = useState<any[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [page, setPage] = useState(Number(query.get('page')) || 0);
  const [showFilter, setShowFilter] = useState(false);
  const [categories, setCategories] = useState<any[]>([]);
  const [provinces, setProvinces] = useState<any[]>([]);
  // filter states
  const [selectedCategory, setSelectedCategory] = useState(query.get('categoryId') || "");
  const [selectedProvince, setSelectedProvince] = useState(query.get('provinceId') || "");
  const [minPriceFilter, setMinPriceFilter] = useState(query.get('minPrice') || "");
  const [maxPriceFilter, setMaxPriceFilter] = useState(query.get('maxPrice') || "");
  const [sortPrice, setSortPrice] = useState(query.get('sortPrice') || "");
  // Lấy danh mục và tỉnh/thành khi mở modal filter
  useEffect(() => {
    if (showFilter) {
      getCategories().then(res => setCategories(res.data.result || []));
      getProvinces().then(res => setProvinces(res.data.data || []));
    }
  }, [showFilter]);
  const size = Number(query.get('size')) || 8;
  const search = query.get('search') || '';
  // const status = query.get('status') ? Number(query.get('status')) : 1;
  const minPrice = query.get('minPrice') ? Number(query.get('minPrice')) : undefined;
  const maxPrice = query.get('maxPrice') ? Number(query.get('maxPrice')) : undefined;
  const sortPriceParam = query.get('sortPrice') || undefined;

  const categoryId = query.get('categoryId') || undefined;
  const provinceId = query.get('provinceId') || undefined;
  useEffect(() => {
    setLoading(true);
    const params: any = { page, size, search, minPrice, maxPrice };
    if (sortPriceParam) params.sortPrice = sortPriceParam;
    if (categoryId) params.categoryId = categoryId;
    if (provinceId) params.provinceId = provinceId;
    searchProduct(params)
      .then(res => {
        setProducts(res.data.result.content || []);
        setTotalPages(res.data.result.totalPages || 1);
      })
      .finally(() => setLoading(false));
  }, [page, size, search, minPrice, maxPrice, sortPriceParam, categoryId, provinceId]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    query.set('page', String(newPage));
    navigate({ search: query.toString() });
  };

  // Xử lý khi ấn nút Áp dụng filter
  const handleApplyFilter = () => {
    // Cập nhật query params
    if (selectedCategory) {
      query.set('categoryId', selectedCategory);
    } else {
      query.delete('categoryId');
    }
    if (selectedProvince) {
      query.set('provinceId', selectedProvince);
    } else {
      query.delete('provinceId');
    }
    if (minPriceFilter) {
      query.set('minPrice', minPriceFilter);
    } else {
      query.delete('minPrice');
    }
    if (maxPriceFilter) {
      query.set('maxPrice', maxPriceFilter);
    } else {
      query.delete('maxPrice');
    }
    if (sortPrice) {
      query.set('sortPrice', sortPrice);
    } else {
      query.delete('sortPrice');
    }
    // Bỏ statusFilter
    // reset page về 0 khi lọc
    query.set('page', '0');
    setPage(0);
    setShowFilter(false);
    navigate({ search: query.toString() });
  };

  return (
    <>
      <Header />
      <div className="max-w-6xl mx-auto py-8 px-4 min-h-[60vh]">
        <div className="flex items-center gap-4 mb-6">
          <h2 className="text-2xl font-bold text-[#cc3333]">Kết quả tìm kiếm cho: <span className="text-gray-800">{search}</span></h2>
          <button
            className="flex items-center gap-2 px-4 py-2 border border-[#cc3333] rounded-full text-[#cc3333] hover:bg-[#cc3333] hover:text-white transition"
            onClick={() => setShowFilter(true)}
          >
            <FaFilter />
            <span>Lọc</span>
          </button>
        </div>
        {/* Modal filter */}
        {showFilter && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
            <div className="bg-white rounded-lg shadow-lg p-6 min-w-[320px] relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-[#cc3333] text-xl"
                onClick={() => setShowFilter(false)}
                aria-label="Đóng"
              >
                ×
              </button>
              <h3 className="text-lg font-bold mb-4 text-[#cc3333]">Bộ lọc sản phẩm</h3>
              {/* Các tiêu chí lọc mẫu */}
              <div className="mb-4">
                <label className="block mb-1 font-medium">Danh mục</label>
                <select
                  className="border rounded px-2 py-1 w-full"
                  value={selectedCategory}
                  onChange={e => setSelectedCategory(e.target.value)}
                >
                  <option value="">Tất cả</option>
                  {categories.map((cat: any) => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block mb-1 font-medium">Địa điểm</label>
                <select
                  className="border rounded px-2 py-1 w-full"
                  value={selectedProvince}
                  onChange={e => setSelectedProvince(e.target.value)}
                >
                  <option value="">Tất cả</option>
                  {provinces.map((prov: any) => (
                    <option key={prov.ProvinceID} value={prov.ProvinceID}>{prov.ProvinceName}</option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block mb-1 font-medium">Khoảng giá</label>
                <div className="flex gap-2">
                  <input
                    type="number"
                    placeholder="Từ"
                    className="border rounded px-2 py-1 w-24"
                    value={minPriceFilter}
                    onChange={e => setMinPriceFilter(e.target.value)}
                  />
                  <input
                    type="number"
                    placeholder="Đến"
                    className="border rounded px-2 py-1 w-24"
                    value={maxPriceFilter}
                    onChange={e => setMaxPriceFilter(e.target.value)}
                  />
                </div>
              </div>
              <div className="mb-4">
                <label className="block mb-1 font-medium">Sắp xếp theo giá</label>
                <select
                  className="border rounded px-2 py-1 w-full"
                  value={sortPrice}
                  onChange={e => setSortPrice(e.target.value)}
                >
                  <option value="">Mặc định</option>
                  <option value="asc">Giá tăng dần</option>
                  <option value="desc">Giá giảm dần</option>
                </select>
              </div>
              <button
                className="bg-[#cc3333] text-white px-4 py-2 rounded-full w-full hover:bg-[#b82d2d] transition"
                onClick={handleApplyFilter}
              >
                Áp dụng
              </button>
            </div>
          </div>
        )}
        {loading ? (
          <div className="text-center text-gray-500 py-12">Đang tải kết quả...</div>
        ) : products.length === 0 ? (
          <div className="text-center text-gray-400 py-12">Không tìm thấy sản phẩm phù hợp.</div>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
            {products.map(product => (
              <ProductCard
                key={product.id}
                id={product.id}
                name={product.name}
                price={Number(product.price)}
                images={product.images}
                rating={Number(product.rating) || 0}
                numberOfOrder={Number(product.numberOfOrder) || 0}
              />
            ))}
          </div>
        )}
        {/* Pagination */}
        {totalPages >= 1 && (
          <div className="flex justify-center mt-8 gap-2">
            {Array.from({ length: totalPages }).map((_, idx) => (
              <button
                key={idx}
                className={`px-4 py-2 rounded-full border ${page === idx ? 'bg-[#cc3333] text-white' : 'bg-white text-gray-700'} font-semibold transition`}
                onClick={() => handlePageChange(idx)}
                disabled={page === idx}
              >
                {idx + 1}
              </button>
            ))}
          </div>
        )}
      </div>
      <Footer />
    </>
  );
};

export default SearchResultPage;