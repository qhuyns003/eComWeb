import React, { useEffect, useState } from 'react';
import Header from '../layout/Header';
import Footer from '../layout/Footer';
import ProductCard from './ProductCard';
import { searchProduct } from '../../api/api';
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
  const size = Number(query.get('size')) || 8;
  const search = query.get('search') || '';
  const status = query.get('status') ? Number(query.get('status')) : 1;
  const minPrice = query.get('minPrice') ? Number(query.get('minPrice')) : undefined;
  const maxPrice = query.get('maxPrice') ? Number(query.get('maxPrice')) : undefined;

  useEffect(() => {
    setLoading(true);
    searchProduct({ page, size, search, status, minPrice, maxPrice })
      .then(res => {
        setProducts(res.data.result.content || []);
        setTotalPages(res.data.result.totalPages || 1);
      })
      .finally(() => setLoading(false));
  }, [page, size, search, status, minPrice, maxPrice]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    query.set('page', String(newPage));
    navigate({ search: query.toString() });
  };

  return (
    <>
      <Header />
      <div className="max-w-6xl mx-auto py-8 px-4 min-h-[60vh]">
        <h2 className="text-2xl font-bold mb-6 text-[#cc3333]">Kết quả tìm kiếm cho: <span className="text-gray-800">{search}</span></h2>
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
        {totalPages > 1 && (
          <div className="flex justify-center mt-8 gap-2">
            {Array.from({ length: totalPages }).map((_, idx) => (
              <button
                key={idx}
                className={`px-4 py-2 rounded-full border ${page === idx ? 'bg-[#cc3333] text-white' : 'bg-white text-gray-700'} font-semibold transition`}
                onClick={() => handlePageChange(idx)}
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