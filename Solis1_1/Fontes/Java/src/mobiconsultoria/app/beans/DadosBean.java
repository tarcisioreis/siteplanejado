package mobiconsultoria.app.beans;

public class DadosBean {
	
	private String txUrl;
	private String txMethod;
	private String txNome;
	private String txEmail;
	private String txTelefone;
	private Image image;
	
	
	public String getTxUrl() {
		return txUrl;
	}
	public void setTxUrl(String txUrl) {
		this.txUrl = txUrl;
	}
	
	public String getTxMethod() {
		return txMethod;
	}
	public void setTxMethod(String txMethod) {
		this.txMethod = txMethod;
	}
	
	public String getTxNome() {
		return txNome;
	}
	public void setTxNome(String txNome) {
		this.txNome = txNome;
	}
	
	public String getTxEmail() {
		return txEmail;
	}
	public void setTxEmail(String txEmail) {
		this.txEmail = txEmail;
	}
	
	public String getTxTelefone() {
		return txTelefone;
	}
	public void setTxTelefone(String txTelefone) {
		this.txTelefone = txTelefone;
	}
	
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	
}
