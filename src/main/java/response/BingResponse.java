package response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BingResponse {
    @SerializedName("images")
    private List<BingImage> bingImages;

    public List<BingImage> getBingImages() {
        return bingImages;
    }
}