package mobiconsultoria.app.splash;

import mobiconsultoria.app.beans.DadosBean;
import mobiconsultoria.app.beans.Image;
import mobiconsultoria.app.constantes.Constantes;
import mobiconsultoria.app.utils.DetectaConexao;
import mobiconsultoria.app.utils.HttpConnection;
import mobiconsultoria.app.utils.Validator;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FormSendActivity extends Activity {

	private EditText edtNome = null;
	private EditText edtEmail = null;
	private EditText edtTelefone = null;
	private ImageView iv = null;
	private View send_status = null;
	private TextView send_status_email = null;
	
	private SendEmailTask mSendEmailTask;
	private DetectaConexao detectaConexao;
	
	private Bitmap img = null;
	private Image image = null;
	private DadosBean dadosBean = null;
	
	private String mime = null;
	private String resposta = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_form_send);
		
		Intent intent = getIntent();
		img = (Bitmap) intent.getParcelableExtra("imagem");
		setImg(img);
		setMime(intent.getStringExtra("mime_img"));
		
		iv = (ImageView) findViewById(R.id.ivFoto);
		
		iv.setImageBitmap(getImg());
		
		edtNome = (EditText) findViewById(R.id.edtNome);
		edtEmail = (EditText) findViewById(R.id.edtEmail);
		edtTelefone = (EditText) findViewById(R.id.edtTelefone);
		
		send_status_email = (TextView) findViewById(R.id.send_status_email);
		send_status = (View) findViewById(R.id.send_status);
	}

	public void enviarFoto(View view) {
		String mEmail = null;
		boolean cancel = false;
		View focusView = null;

		if (mSendEmailTask != null) {
			return;
		}
		
		edtNome.setError(null);
		edtEmail.setError(null);
		edtTelefone.setError(null);

		if (!Validator.validateNotNull(edtNome, getString(R.string.error_name_invalid))) {
			focusView = edtNome;
			cancel = true;
		}
		
		mEmail = edtEmail.getText().toString();
		
		if (!Validator.validateEmail(mEmail) && !cancel) {
			edtEmail.setError(getString(R.string.error_email_invalid));
			edtEmail.setFocusable(true);
			edtEmail.requestFocus();

			focusView = edtEmail;
			cancel = true;
		}

		if (!Validator.validateNotNull(edtTelefone, getString(R.string.error_telefone_invalid)) && !cancel) {
			focusView = edtTelefone;
			cancel = true;
		}
		
		if (cancel) {
			focusView.requestFocus();
		} else {
			detectaConexao = new DetectaConexao(getApplicationContext());
			
			if (!detectaConexao.existeConexao()) {
				Toast.makeText(getApplicationContext(), getString(R.string.msg_network_status), Toast.LENGTH_LONG).show();
				return;
			}

			image = new Image();
			dadosBean = new DadosBean();
			
			dadosBean.setTxUrl(Constantes.URL);
			dadosBean.setTxMethod("save-form");

			dadosBean.setTxNome(edtNome.getText().toString());
			dadosBean.setTxEmail(mEmail);
			dadosBean.setTxTelefone(edtTelefone.getText().toString());
			
			image.setMime(getMime());
			image.setBitmap(getImg());
			
			dadosBean.setImage(image);
			
			send_status_email.setText(R.string.msg_wait_email);
			showProgress(true);
			
			mSendEmailTask = new SendEmailTask();
			mSendEmailTask.execute(dadosBean);

/*			
			new Thread(){
				public void run(){
					resposta = HttpConnection.getSetDataWeb(dadosBean);
					
					runOnUiThread(new Runnable(){
						public void run(){
							btSendEmail.setEnabled(false);
							showProgress(true);
							try{
								resposta = Integer.parseInt(resposta) == 1 ? getString(R.string.msg_send_email) : getString(R.string.msg_fail_email);
								Toast.makeText(FormSendActivity.this, resposta, Toast.LENGTH_SHORT).show();
							}
							catch(NumberFormatException e){ e.printStackTrace(); }
							showProgress(false);
						}
					});
				}
			}.start();
*/			
		
//			finish();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			send_status.setVisibility(View.VISIBLE);
			send_status.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							send_status_email.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			send_status.setVisibility(View.VISIBLE);
			send_status.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							send_status.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			send_status.setVisibility(show ? View.VISIBLE : View.GONE);
			send_status.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public class SendEmailTask extends AsyncTask<DadosBean, Void, Boolean> {
		@Override
		protected Boolean doInBackground(DadosBean... params) {
			resposta = HttpConnection.getSetDataWeb(params[0]);
			
			return (Integer.parseInt(resposta) == 1 ? true : false);
		}

		@Override
		protected void onPostExecute(Boolean success) {
			mSendEmailTask = null;
			showProgress(false);

			if (success) {
				send_status_email.setText(R.string.msg_send_email);			
				
				finish();
			} else {
				send_status_email.setText(R.string.msg_fail_email);
			}
		}

		@Override
		protected void onCancelled() {
			mSendEmailTask = null;
			showProgress(false);
		}
	
	}
	
	public Bitmap getImg() {
		return img;
	}
	public void setImg(Bitmap img) {
		this.img = img;
	}

	public String getMime() {
		return mime;
	}
	public void setMime(String mime) {
		this.mime = mime;
	}
	
}
