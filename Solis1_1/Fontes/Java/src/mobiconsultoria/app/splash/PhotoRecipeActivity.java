package mobiconsultoria.app.splash;

import java.io.File;

import mobiconsultoria.app.beans.DadosBean;
import mobiconsultoria.app.beans.Image;
import mobiconsultoria.app.constantes.Constantes;
import mobiconsultoria.app.utils.DetectaConexao;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoRecipeActivity extends Activity {

	private ImageView iv = null;
	private ImageButton btCameraGallery = null;
	private ImageButton btCameraDelete = null;
	private ImageButton btCameraTake = null;
	private Bitmap img = null;
	
	private File file = null;
	private DadosBean dadosBean = null;
	private Image image = null;
	private DetectaConexao detectaConexao = null;
    private boolean fotoOK = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_recipe);
		
		dadosBean = new DadosBean();
		image = new Image();
		
		iv = (ImageView) findViewById(R.id.ivFoto);
		
		btCameraGallery = (ImageButton) findViewById(R.id.btCameraGallery); 
		btCameraTake = (ImageButton) findViewById(R.id.btCameraTake);
		btCameraDelete = (ImageButton) findViewById(R.id.btCameraDelete);

		setFotoOK(false);
		
		detectaConexao = new DetectaConexao(getApplicationContext());
		
		if (!detectaConexao.existeConexao()) {
			Toast.makeText(getApplicationContext(), getString(R.string.msg_network_status), Toast.LENGTH_LONG).show();
		}
	}
	
	// Ao clicar na foto para enviar
	public void prepararEnvioFoto(View view) {
		if (isFotoOK()) {
			try {
				Intent intent = new Intent(this, FormSendActivity.class);
			
				intent.putExtra("imagem", dadosBean.getImage().getBitmap());
				intent.putExtra("mime_img", dadosBean.getImage().getMime());
			
				startActivity(intent);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Abrir galeria de fotos existentes
	public void abrirGaleria(View view) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		startActivityForResult(intent, Constantes.IMAGEM_INTERNA);
	}

	// Tirar foto
	public void tirarFoto(View view){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, Constantes.IMAGEM_CAMERA);
	}
	
	// Deletar fotos
	public void deletarFoto(View view) {
		if (isFotoOK()) {
			img = null;
			setImagem(img);
			iv.setImageResource(R.drawable.ic_background);
			setFotoOK(false);
		}
		
		btCameraTake.setEnabled(!isFotoOK());
		btCameraGallery.setEnabled(!isFotoOK());
		
		btCameraDelete.setEnabled(isFotoOK());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		try {
		if(requestCode == Constantes.IMAGEM_INTERNA) {
			if(resultCode == RESULT_OK){
				
				Uri imagemSelecionada = data.getData();
				
				String[] colunas = {MediaStore.Images.Media.DATA};
				
				Cursor cursor = getContentResolver().query(imagemSelecionada, colunas, null, null, null);
				cursor.moveToFirst();
				
				int indexColuna = cursor.getColumnIndex(colunas[0]);
				String pathImg = cursor.getString(indexColuna);
				cursor.close();
				
				img = BitmapFactory.decodeFile(pathImg);

				file = new File(pathImg);
				if (file != null) {
					image.setBitmap(img);
					
					dadosBean.setImage(image);
					
					dadosBean.getImage().setResizedBitmap(file, 300, 300);
					dadosBean.getImage().setMimeFromImgPath(file.getPath());
				}
				
				iv.setImageBitmap(img);
				
				setFotoOK(true);
			    
			}
		} else if (requestCode == Constantes.IMAGEM_CAMERA) {
				   if(data != null){
					   
					  Bundle bundle = data.getExtras();
					  if(bundle != null){
						 img = (Bitmap) bundle.get("data");

						 if (img != null) {
							 image.setBitmap(img);
							 image.setMime("png");

							 dadosBean.setImage(image);
						 }
						 
						 iv.setImageBitmap(img);
						 
						 setFotoOK(true);
						 
					  }
					  
				   }
		}
		
		btCameraTake.setEnabled(!isFotoOK());
		btCameraGallery.setEnabled(!isFotoOK());
		
		btCameraDelete.setEnabled(isFotoOK());
		
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), getString(R.string.msg_exception_status) + " Erro: " + e.getMessage() + " - " + requestCode, Toast.LENGTH_LONG).show();
		}
	}
	
    public boolean isFotoOK() {
		return fotoOK;
	}
	public void setFotoOK(boolean fotoOK) {
		this.fotoOK = fotoOK;
	}
	
	public Bitmap getImagem() {
		return img;
	}
	public void setImagem(Bitmap img) {
		this.img = img;
	}
	
}
