import React, { useEffect, useState } from 'react';
import { getShopInfo, registerShop } from '../../api/api';
import { toast } from 'react-toastify';

interface ShopInfoEditProps {
  shopId: string;
}

const ShopInfoEdit: React.FC<ShopInfoEditProps> = ({ shopId }) => {
  const [shop, setShop] = useState<any>(null);
  const [form, setForm] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [editMode, setEditMode] = useState(false);

  useEffect(() => {
    getShopInfo([shopId]).then(res => {
      const info = res.data?.data?.[0] || null;
      setShop(info);
      setForm(info);
    });
  }, [shopId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      await registerShop(form); // Giả sử API này cho phép update nếu đã có shopId
      toast.success('Cập nhật thông tin shop thành công!');
      setEditMode(false);
      setShop(form);
    } catch (err: any) {
      toast.error('Cập nhật thất bại!');
    } finally {
      setLoading(false);
    }
  };

  if (!shop) return <div>Đang tải thông tin shop...</div>;

  return (
    <div className="bg-white rounded-2xl shadow-xl p-8 max-w-xl mx-auto mt-8">
      <h2 className="text-2xl font-bold text-[#cc3333] mb-4">Thông tin shop</h2>
      {!editMode ? (
        <div className="space-y-3">
          <div><span className="font-medium">Tên shop:</span> {shop.name}</div>
          <div><span className="font-medium">Mô tả:</span> {shop.description}</div>
          <div><span className="font-medium">Địa chỉ:</span> {shop.fullAddress}</div>
          <button onClick={() => setEditMode(true)} className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform mt-4">Sửa thông tin</button>
        </div>
      ) : (
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="block font-medium mb-1">Tên shop</label>
            <input name="name" value={form.name} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
          </div>
          <div>
            <label className="block font-medium mb-1">Mô tả</label>
            <textarea name="description" value={form.description} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
          </div>
          <div>
            <label className="block font-medium mb-1">Địa chỉ</label>
            <input name="fullAddress" value={form.fullAddress} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
          </div>
          <div className="flex gap-3 mt-2">
            <button type="submit" className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform flex-1" disabled={loading}>
              {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
            </button>
            <button type="button" onClick={() => setEditMode(false)} className="bg-gray-100 text-gray-700 px-6 py-2 rounded-full font-semibold hover:bg-gray-200 transition flex-1">Hủy</button>
          </div>
        </form>
      )}
    </div>
  );
};

export default ShopInfoEdit;
