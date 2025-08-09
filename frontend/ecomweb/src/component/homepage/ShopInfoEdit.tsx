import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getShopInfoByUserId, getProvinces, getDistricts, getWards, updateShop } from '../../api/api';
import { toast } from 'react-toastify';
import Header from '../layout/Header';
import Footer from '../layout/Footer';

const ShopInfoEdit: React.FC = () => {
  const navigate = useNavigate();
  const [shop, setShop] = useState<any>(null);
  const [form, setForm] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [provinces, setProvinces] = useState<any[]>([]);
  const [districts, setDistricts] = useState<any[]>([]);
  const [wards, setWards] = useState<any[]>([]);

  useEffect(() => {
    getShopInfoByUserId().then(res => {
      const info = res.data?.result || null;
      setShop(info);
      setForm({
        name: info?.name || '',
        description: info?.description || '',
        phoneNumber: info?.shopAddressResponse?.phoneNumber || '',
        detailAddress: info?.shopAddressResponse?.detailAddress || '',
        province: info?.shopAddressResponse?.province || '',
        provinceId: info?.shopAddressResponse?.provinceId || '',
        district: info?.shopAddressResponse?.district || '',
        districtId: info?.shopAddressResponse?.districtId || '',
        ward: info?.shopAddressResponse?.ward || '',
        wardCode: info?.shopAddressResponse?.wardCode || '',
      });
      getProvinces().then(res => setProvinces(res.data.data || []));
    });
  }, []);

  useEffect(() => {
    if (form?.provinceId) {
      getDistricts(Number(form.provinceId)).then(res => setDistricts(res.data.data || []));
    } else {
      setDistricts([]);
    }
  }, [form?.provinceId]);

  useEffect(() => {
    if (form?.districtId) {
      getWards(Number(form.districtId)).then(res => setWards(res.data.data || []));
    } else {
      setWards([]);
    }
  }, [form?.districtId]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((prev: any) => ({ ...prev, [name]: value }));
  };

  const handleProvinceChange = async (e: React.ChangeEvent<HTMLSelectElement>) => {
    const provinceId = e.target.value;
    const provinceObj = provinces.find((p: any) => String(p.ProvinceID) === provinceId);
    setForm((f: any) => ({ ...f, province: provinceObj?.ProvinceName || '', provinceId, district: '', districtId: '', ward: '', wardCode: '' }));
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
    setForm((f: any) => ({ ...f, district: districtObj?.DistrictName || '', districtId, ward: '', wardCode: '' }));
    setWards([]);
    if (districtId) {
      const res = await getWards(Number(districtId));
      setWards(res.data.data || []);
    }
  };
  const handleWardChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const wardCode = e.target.value;
    const wardObj = wards.find((w: any) => String(w.WardCode) === wardCode);
    setForm((f: any) => ({ ...f, ward: wardObj?.WardName || '', wardCode }));
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      // Build address object rõ ràng
      const shopData = {
        ...form,
        fullAddress: `${form.detailAddress}, ${form.ward}, ${form.district}, ${form.province}`,
      };
      await updateShop(shopData);
      toast.success('Cập nhật thông tin shop thành công!');
      setShop(shopData);
      setTimeout(() => {
        navigate('/profile');
      }, 800);
    } catch (err: any) {
      toast.error('Cập nhật thất bại!');
    } finally {
      setLoading(false);
    }
  };

  if (!shop || !form) return <div>Đang tải thông tin shop...</div>;

  return (
    <>
      <Header />
      <div className="max-w-xl mx-auto mt-8">
        <button type="button" onClick={() => navigate('/profile')} className="mb-2 flex items-center text-sm text-gray-600 hover:text-[#cc3333] font-medium">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" /></svg>
          Quay lại
        </button>
        <form onSubmit={handleSave} className="bg-white rounded-2xl shadow-xl p-8 space-y-4">
          <h2 className="text-2xl font-bold text-[#cc3333] mb-4">Thông tin shop</h2>
          <div>
          <label className="block font-medium mb-1">Tên shop</label>
          <input name="name" value={form.name} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
        </div>
        <div>
          <label className="block font-medium mb-1">Mô tả</label>
          <textarea name="description" value={form.description} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
        </div>
        <div>
          <label className="block font-medium mb-1">Số điện thoại</label>
          <input name="phoneNumber" value={form.phoneNumber} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
        </div>
        <div>
          <label className="block font-medium mb-1">Địa chỉ chi tiết</label>
          <input name="detailAddress" value={form.detailAddress} onChange={handleChange} className="border border-gray-300 rounded-lg px-3 py-2 w-full" />
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
        <div className="flex gap-3 mt-2">
          <button type="submit" className="bg-gradient-to-r from-yellow-500 to-yellow-600 text-white px-6 py-2 rounded-full font-semibold hover:scale-105 transition-transform flex-1" disabled={loading}>
            {loading ? 'Đang lưu...' : 'Lưu thay đổi'}
          </button>
        </div>
        </form>
      </div>
      <Footer />
    </>
  );
};

export default ShopInfoEdit;
