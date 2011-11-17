package io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class QuizeFileManager {

	/** 読み込みバッファ */
	private BufferedReader br;

	/** 書き込みバッファ */
	private BufferedWriter bw;

	public QuizeFileManager() {
		try{
			FileReader fr = new FileReader("C:\\Users\\kitazawa\\Dropbox\\Document\\devquiz2011\\スライドパズル\\problems.txt");
			FileWriter fw = new FileWriter("C:\\Users\\kitazawa\\Dropbox\\Document\\devquiz2011\\スライドパズル\\answer.txt");
			br = new BufferedReader(fr);
			bw = new BufferedWriter(fw);

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/** 新しく読み込んだ行を返す
	 * 	ファイル末尾の場合はnullを返す */
	public String getNewLine(){
		String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return line;
	}

	/** 答えを書き込む */
	public void writeAnswer(String s){
		try {
			bw.write(s+"\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
