package andrades.isabelly.appgaleriapublica;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GalleryRepository {

    Context context;

    public GalleryRepository(Context context){
        this.context = context;
    }

    // retorna lista de imagens para uma página
    public List<ImageData> loadImageData(Integer limit, Integer offSet) throws FileNotFoundException {
        // lista de dados da imagem
        List<ImageData> imageDataList = new ArrayList<>();

        // tamanhos que as imagens devem ter
        int w = (int) context.getResources().getDimension(R.dimen.im_width);
        int h = (int) context.getResources().getDimension(R.dimen.im_height);

        // acessa a tabela que guarda as imagens no celular
        String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_ADDED, MediaStore.Images.Media.SIZE};

        // declara as variáveis que vão armazenar o que desejamos selecionar
        String selection = null;
        String selectionArg[] = null;
        String sort = MediaStore.Images.Media.DATE_ADDED;
        Cursor cursor = null;

        // verifica se a versão do android é maior que a 11
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {

            Bundle queryArgs = new Bundle();
            // define o valor de selection
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
            // define o valor de selectionArg
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArg);

            // define as colunas que serão usadas
            queryArgs.putString(ContentResolver.QUERY_ARG_SORT_COLUMNS, sort);
            // define qual a direção da orientação
            queryArgs.putInt(ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_ASCENDING);

            // define o limit e o offset
            queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, limit);
            queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, offSet);

            // realiza a consulta
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, queryArgs, null);
        } else {
            // para aparelhos com android infeiror 11, realiza a consulta
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, selection, selectionArg,
                    sort + " ASC + LIMIT " + String.valueOf(limit) + " OFFSET " + String.valueOf(offSet));
        }

        // cria as variáveis para armazenar os dados da foto
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

        while (cursor.moveToNext()) {
            // obtem os valores da foto
            long id = cursor.getLong(idColumn);
            Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            String name = cursor.getString(nameColumn);
            int dateAdded = cursor.getInt(dateAddedColumn);
            int size = cursor.getInt(sizeColumn);

            // cria a thumb para a foto
            Bitmap thumb = Util.getBitmap(context, contentUri, w, h);

            // pega os dados da imagem e os armazena na lista
            imageDataList.add(new ImageData(contentUri, thumb, name, new Date(dateAdded*1000L), size));
        }
        // retorna a lista de imagens para uma pagina
        return imageDataList;
    }

    }