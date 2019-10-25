package cn.itcast.mydiyview.bean;

public class MainListBran {
    private int mImageView;
    private String content;

    public MainListBran(int imageView,String content) {
        this.mImageView = imageView;
        this.content = content;
    }

    public int getImageView() {
        return mImageView;
    }

    public void setImageView(int imageView) {
        mImageView = imageView;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
