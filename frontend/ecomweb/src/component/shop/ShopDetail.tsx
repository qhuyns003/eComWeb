import { useAppSelector } from "../../store/hooks";
import { selectUser } from "../../store/features/userSlice";

import React, { useEffect, useState } from "react";
import Header from "../layout/Header";
import Footer from "../layout/Footer";
import defaultAvatar from "../../assets/default-avatar-icon-of-social-media-user-vector.jpg";
import { useParams } from "react-router-dom";
import { getShopById, getProductsByShopId, sendMessage, createPrivateChat } from "../../api/api";
import ProductCard from "../homepage/ProductCard";
import ChatBox from "../chat/ChatBox";

const ShopDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [shop, setShop] = useState<any>({});
  const [products, setProducts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [showContactModal, setShowContactModal] = useState(false);
  const [contactContent, setContactContent] = useState("Xin chào! Tôi muốn liên hệ với bạn");
  const [showChatBox, setShowChatBox] = useState(false);
  const [chatRoomId, setChatRoomId] = useState<string | null>(null);
  const currentUser = useAppSelector(selectUser);

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    Promise.all([
      getShopById(id),
      getProductsByShopId(id)
    ])
      .then(([shopRes, prodRes]) => {
        if (shopRes.data && shopRes.data.code === 1000) {
          const data = shopRes.data.result;
          setShop({
            shopId: data.id,
            name: data.name ,
            avatar:  defaultAvatar,
            description: data.description || "Chưa có mô tả",
            address: data.shopAddressResponse?.fullAddress || "Chưa cập nhật",
            phone: data.shopAddressResponse?.phoneNumber || "Chưa cập nhật",
            email: data.email || "",
            createdAt: data.createdAt || '',
            status: data.status || '',
            province: data.shopAddressResponse?.province || '',
            district: data.shopAddressResponse?.district || '',
          });
        } else {
          setError("Không tìm thấy shop.");
        }
        if (prodRes.data && prodRes.data.code === 1000) {
          setProducts(prodRes.data.result || []);
        }
      })
      .catch(() => setError("Lỗi khi tải thông tin shop hoặc sản phẩm."))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-[#cc3333] to-pink-500"><span>Đang tải...</span></div>;
  if (error) return <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-[#cc3333] to-pink-500"><span>{error}</span></div>;

  return (
    <div className="min-h-screen flex flex-col bg-gradient-to-r from-[#cc3333] to-pink-500">
      <Header />
      <div className="flex-grow">
        <div className="max-w-5xl mx-auto w-full bg-white rounded-lg shadow-lg mt-8 p-8 flex flex-col items-center">
          <img
            src={defaultAvatar}
            alt="avatar"
            className="w-32 h-32 rounded-full border-4 border-pink-400 shadow mb-4 object-cover"
            onError={e => { (e.currentTarget as HTMLImageElement).src = defaultAvatar; }}
          />
          <h2 className="text-3xl font-bold text-[#cc3333] mb-2">{shop.name || ''}</h2>
          <p className="text-gray-600 mb-2">{shop.description || ''}</p>
          <div className="flex flex-col md:flex-row gap-4 text-gray-700 mb-6">
            <span><b>Địa chỉ:</b> {shop.address || ''}</span>
            {shop.createdAt && <span><b>Ngày tạo:</b> {new Date(shop.createdAt).toLocaleDateString()}</span>}
          </div>
          <button
            className="mb-6 bg-[#cc3333] text-white px-6 py-2 rounded-full hover:bg-pink-500 transition font-semibold"
            onClick={() => setShowContactModal(true)}
          >
            Liên hệ với shop
          </button>

          {/* Modal liên hệ */}
          {showContactModal && (
            <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40">
              <div className="bg-white rounded-lg shadow-lg p-6 w-full max-w-md relative">
                <button className="absolute top-2 right-3 text-gray-500 hover:text-red-500 text-xl" onClick={() => setShowContactModal(false)}>&times;</button>
                <h2 className="text-xl font-bold mb-4 text-[#cc3333]">Liên hệ với shop</h2>
                <textarea
                  className="w-full border rounded p-2 mb-4 min-h-[80px]"
                  value={contactContent}
                  onChange={e => setContactContent(e.target.value)}
                />
                <div className="flex justify-end gap-2">
                  <button
                    className="px-4 py-2 rounded bg-gray-200 text-gray-700 hover:bg-gray-300"
                    onClick={() => setShowContactModal(false)}
                  >Hủy</button>
                  <button
                    className="px-4 py-2 rounded bg-[#cc3333] text-white hover:bg-pink-500"
                    onClick={async () => {
                      if (!contactContent.trim() || !shop.shopId || !currentUser?.id) return;
                      // 1. Gọi API tạo private chat để lấy roomId
                      const chatRes = await createPrivateChat(shop.shopId);
                      const roomId = chatRes?.data?.result?.roomId || chatRes?.data?.result || chatRes?.data?.roomId;
                      if (!roomId) {
                        alert('Không thể tạo phòng chat!');
                        return;
                      }
                      // 2. Gửi tin nhắn với payload đúng định dạng, có sender
                      await sendMessage({
                        key: { roomId },
                        sender: currentUser.id,
                        content: contactContent,
                        type: 'text',
                      });
                      setShowContactModal(false);
                      setChatRoomId(roomId);
                      setShowChatBox(true);
                    }}
                  >Gửi</button>
                </div>
              </div>
            </div>
          )}

          {/* Hiện ChatBox sau khi gửi */}
          {showChatBox && chatRoomId && (
            <ChatBox roomId={chatRoomId} roomName={shop.name || "Shop"} onClose={() => setShowChatBox(false)} />
          )}
          <h3 className="text-2xl font-semibold text-[#cc3333] mb-4 mt-4">Sản phẩm nổi bật</h3>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 w-full">
            {products.length === 0 && <div className="col-span-3 text-center text-gray-400">Chưa có sản phẩm</div>}
            {products.map((product, idx) => (
              <ProductCard
                key={product.id + '-' + idx}
                id={product.id}
                name={product.name}
                price={product.price}
                images={product.images}
                rating={product.rating}
                numberOfOrder={product.numberOfOrder}
              />
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};

export default ShopDetail;
