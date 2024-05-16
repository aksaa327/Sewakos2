package com.example.sewakos;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class AndroidUtil implements Parcelable {

    private String id;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private String imageURL;
    private String namaKos;
    private String deskripsiKos;
    private String catatanAlamatKos;
    private String fasilitasKos;
    private String tipeKos;
    private Integer ketersediaanKamarKos, hargaKos;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getNamaKos() {
        return namaKos;
    }

    public void setNamaKos(String namaKos) {
        this.namaKos = namaKos;
    }

    public String getDeskripsiKos() {
        return deskripsiKos;
    }

    public void setDeskripsiKos(String deskripsiKos) {
        this.deskripsiKos = deskripsiKos;
    }

    public String getCatatanAlamatKos() {
        return catatanAlamatKos;
    }

    public void setCatatanAlamatKos(String catatanAlamatKos) {
        this.catatanAlamatKos = catatanAlamatKos;
    }

    public String getFasilitasKos() {
        return fasilitasKos;
    }

    public void setFasilitasKos(String fasilitasKos) {
        this.fasilitasKos = fasilitasKos;
    }

    public String getTipeKos() {
        return tipeKos;
    }

    public void setTipeKos(String tipeKos) {
        this.tipeKos = tipeKos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getKetersediaanKamarKos() {
        return ketersediaanKamarKos;
    }

    public void setKetersediaanKamarKos(Integer ketersediaanKamarKos) {
        this.ketersediaanKamarKos = ketersediaanKamarKos;
    }

    public Integer getHargaKos() {
        return hargaKos;
    }

    public void setHargaKos(Integer hargaKos) {
        this.hargaKos = hargaKos;
    }

    public AndroidUtil() {
        // Default constructor required for calls to DataSnapshot.getValue(AndroidUtil.class)
    }

    public AndroidUtil(String imageURL, String namaKos, String deskripsiKos, String catatanAlamatKos, String fasilitasKos, String tipeKos, Integer ketersediaanKamarKos, Integer hargaKos) {
        this.imageURL = imageURL;
        this.namaKos = namaKos;
        this.deskripsiKos = deskripsiKos;
        this.catatanAlamatKos = catatanAlamatKos;
        this.fasilitasKos = fasilitasKos;
        this.tipeKos = tipeKos;
        this.ketersediaanKamarKos = ketersediaanKamarKos;
        this.hargaKos = hargaKos;
    }

    protected AndroidUtil(Parcel in) {
        id = in.readString();
        catatanAlamatKos = in.readString();
        deskripsiKos = in.readString();
        fasilitasKos = in.readString();
        hargaKos = in.readInt();
        imageURL = in.readString();
        ketersediaanKamarKos = in.readInt();
        namaKos = in.readString();
        tipeKos = in.readString();
    }

    public static final Creator<AndroidUtil> CREATOR = new Creator<AndroidUtil>() {
        @Override
        public AndroidUtil createFromParcel(Parcel in) {
            return new AndroidUtil(in);
        }

        @Override
        public AndroidUtil[] newArray(int size) {
            return new AndroidUtil[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(catatanAlamatKos);
        dest.writeString(deskripsiKos);
        dest.writeString(fasilitasKos);
        dest.writeInt(hargaKos);
        dest.writeString(imageURL);
        dest.writeInt(ketersediaanKamarKos);
        dest.writeString(namaKos);
        dest.writeString(tipeKos);
    }

    public static void setProfilePic(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);
    }

}
