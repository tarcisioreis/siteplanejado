<?php
	session_start();
	
	if(preg_match('/^(save-form){1}$/', $_POST['method'])){
		
		$nome = $_POST["nome"];
		$email = $_POST["email"];
		$telefone = $_POST["telefone"];
		$mime = $_POST['img-mime'];

		$imgName = 'img-'.time().'.'.$mime;
		
/*		
		$f = fopen('DATA.txt', 'w');
		fwrite($f, "Name: ".$_POST['nome']."\n");
		fwrite($f, "Email: ".$_POST['email']."\n");
		fwrite($f, "Telefone: ".$_POST['telefone']."\n");
		fwrite($f, "Mime: ".$_POST['img-mime']."\n");
		fwrite($f, "Image: ".$imgName."\n");
		fclose($f);
*/		
		$binary = base64_decode($_POST['img-image']);
		$file = fopen('../img/'.$imgName, 'wb');
		fwrite($file, $binary);
		fclose($file);
		
		$fp = fopen('../img/'.$imgName, "rb"); // abre o arquivo enviado
		$anexo = fread($fp, filesize('../img/'.$imgName)); // calcula o tamanho
		$anexo = base64_encode($anexo); // codifica o anexo em base 64
		fclose($fp); // fecha o arquivo
		
		if ($mime == "jpg") {
			$type = "image/jpeg"; 
		} else {
			$type = "image/png"; 
		}
		
		$boundary = md5(uniqid(time()));

// Destinatário do Email
		$para = "comercial@siteplanejado.com.br";

		$mensagem  = "<strong>Nome:  </strong>".$nome;
		$mensagem .= "<br>  <strong>E-mail: </strong>".$email;
		$mensagem .= "<br>  <strong>Telefone de contato: </strong>".$telefone;
		$mensagem .= "<br>  <strong>Mensagem: </strong>Não responder este e-mail";
 
		$mens  = "--".$boundary."\n";
		$mens .= "Content-Transfer-Encoding: 8bits\n";
		$mens .= "Content-Type: text/html; charset=\"UTF-8\"\n\n"; //Codifica para UTF
		$mens .= $mensagem."\n"; // Mensagem ou Corpo do Email

		$mens .= "--".$boundary."\n";
		$mens .= "Content-Type: ".$type."; name=\"". $imgName . "\"\n";
		$mens .= "Content-Disposition: attachment; filename=\"".$imgName."\"\n";
		$mens .= "Content-Transfer-Encoding: base64\n\n";
		$mens .= "$anexo\n";
		$mens .= "--".$boundary."--\r\n";
 
		$headers  = "MIME-Version: 1.0\n";
		$headers .= "From: ".$nome." <".$email.">\r\n"; // Nome e Email de quem Enviou
		$headers .= "Content-type: multipart/mixed; boundary=".$boundary."\r\n";
		$headers .= $boundary."\n";
 
		$assunto = "Captura de imagem"; // Assunto
	
		$flag = mail($para, $assunto, $mens, $headers); // envia o email

		if ($flag) {
			echo "1";
		} else {
			echo "0";
		}
	
		unlink($imgName);
		
	}
?>