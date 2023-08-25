package andrades.isabelly.appgaleriapublica;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GalleryRepsitory {

    Context context;

    public GalleryRepsitory(Context context){
        this.context = context;
    }
    public List<ImageData> loadImageData(Integer limit, Integer offSet) throws FileNotFoundException {

        List<ImageData> loadImageList = new ArrayList<>();

        int w = (int) context.getResources().getDimension(R.dimen.im_width);
        int h = (int) context.getResources().getDimension(R.dimen.im_height);

        // linha 14
    }