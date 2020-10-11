package PostPc.Ask.projectapp;

import android.net.Uri;

public class ImageDetails {

    private Uri imageUrl;
    private String imageId;

    public ImageDetails(Uri imageUrl, String Id) {
        this.imageUrl = imageUrl;
        this.imageId = Id;
    }

    public String getImageId() {
        return imageId;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }
}
