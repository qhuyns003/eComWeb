import React, { useState, useEffect } from 'react';
import { FaStore } from 'react-icons/fa';
import { getProvinces, getDistricts, getWards } from '../../api/api';

interface RegisterShopFormProps {
  onSubmit?: (data: any) => void;
  loading?: boolean;
}

const RegisterShopForm: React.FC<RegisterShopFormProps> = ({ onSubmit, loading }) => {
  const [form, setForm] = useState({
    name: '',
    description: '',
    address: '',
    province: '',
    provinceId: '',
    district: '',
    districtId: '',
    ward: '',
    wardCode: '',
  });
  const [provinces, setProvinces] = useState<any[]>([]);
  const [districts, setDistricts] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);

  useEffect(() => {
    getProvinces().then(res => setProvinces(res.data.data || []));
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleProvinceChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const provinceId = e.target.value;
    const provinceObj = provinces.find((p: any) => String(p.ProvinceID) === provinceId);
    setForm(f => ({ ...f, province: provinceObj?.ProvinceName || '', provinceId, district: '', districtId: '', ward: '', wardCode: '' }));
    setDistricts([]);
    setWards([]);
    if (provinceId) {
      const res = await getDistricts(Number(provinceId));
      setDistricts(res.data.data || []);
    }
  };

  const handleDistrictChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const districtId = e.target.value;
    const districtObj = districts.find((d: any) => String(d.DistrictID) === districtId);
    setForm(f => ({ ...f, district: districtObj?.DistrictName || '', districtId, ward: '', wardCode: '' }));
    setWards([]);
    if (districtId) {
      const res = await getWards(Number(districtId));
      setWards(res.data.data || []);
    }
  };

  const handleWardChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const wardCode = e.target.value;
    const wardObj = wards.find((w: any) => String(w.WardCode) === wardCode);
    setForm(f => ({ ...f, ward: wardObj?.WardName || '', wardCode }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Gộp địa chỉ chi tiết
    const fullAddress = `${form.address}, ${form.ward}, ${form.district}, ${form.province}`;
    if (onSubmit) onSubmit({
      ...form,
      fullAddress,
    });
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4 max-w-md mx-auto p-8 bg-white rounded-2xl shadow-2xl border border-yellow-200 relative animate-fadeIn">
      <div className="flex flex-col items-center mb-2">
        <div className="bg-gradient-to-r from-yellow-400 to-yellow-600 rounded-full p-4 mb-2 shadow-lg">
          <FaStore className="text-white text-3xl" />
        </div>
        <h2 className="text-2xl font-bold text-[#cc3333] mb-1 tracking-tight">Đăng ký kênh bán</h2>
        <p className="text-gray-500 text-sm mb-2 text-center max-w-xs">Tạo shop để bắt đầu kinh doanh, tiếp cận hàng ngàn khách hàng và quản lý sản phẩm dễ dàng!</p>
      </div>
      <div>
        <label className="block font-medium mb-1">Tên shop <span className="text-red-500">*</span></label>
        <input name="name" value={form.name} onChange={handleChange} required className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md" />
      </div>
      <div>
        <label className="block font-medium mb-1">Mô tả</label>
        <textarea name="description" value={form.description} onChange={handleChange} rows={3} className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md" />
      </div>
      <div>
        <label className="block font-medium mb-1">Địa chỉ chi tiết <span className="text-red-500">*</span></label>
        <input name="address" value={form.address} onChange={handleChange} required className="border border-gray-300 rounded-lg px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md" placeholder="Số nhà, tên đường..." />
      </div>
      <div className="grid grid-cols-3 gap-4">
        <select name="provinceId" value={form.provinceId || ''} onChange={handleProvinceChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md">
          <option value="">Tỉnh/Thành</option>
          {provinces.map((p: any) => (
            <option key={p.ProvinceID} value={p.ProvinceID}>{p.ProvinceName}</option>
          ))}
        </select>
        <select name="districtId" value={form.districtId || ''} onChange={handleDistrictChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md" disabled={!form.provinceId}>
          <option value="">Quận/Huyện</option>
          {districts.map((d: any) => (
            <option key={d.DistrictID} value={d.DistrictID}>{d.DistrictName}</option>
          ))}
        </select>
        <select name="wardCode" value={form.wardCode || ''} onChange={handleWardChange} className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-yellow-500 transition-shadow hover:shadow-md" disabled={!form.districtId}>
          <option value="">Phường/Xã</option>
          {wards.map((w: any) => (
            <option key={w.WardCode} value={w.WardCode}>{w.WardName}</option>
          ))}
        </select>
      </div>
      <button type="submit" className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform w-full shadow" disabled={loading}>
        {loading ? 'Đang đăng ký...' : 'Đăng ký'}
      </button>
    </form>
  );
};

export default RegisterShopForm;
