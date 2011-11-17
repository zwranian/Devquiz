package puzzle;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuizeManager {

	/** クイズを保持するためのArrayList */
	ArrayList<Puzzle> puzzleList;

	/** 現在のクイズの番号 */
	int index=0;

	/** 総問題数*/
	int PuzzleNum;

	/** 現在の移動可能ステータス */
	int[] statusList;

	public QuizeManager(String[] statusList,String PuzzleNum){
		this.statusList = new int[4];

		for (int i=0;i<statusList.length;i++){
			this.statusList[i] = Integer.parseInt(statusList[i]);
		}
		this.PuzzleNum = Integer.parseInt(PuzzleNum);

		puzzleList = new ArrayList<Puzzle>();
	}

	/** 文字列を指定してPuzzleを生成 */
	public void setPuzzle(String line){

		puzzleList.add(new Puzzle(line));
	}

	/** 次のクイズを取得 */
	public Puzzle getNextPuzzle(){
		Puzzle tmp=null;
		if(index < PuzzleNum){
			tmp = puzzleList.get(index++);
		}
		return tmp;
	}

	/**
	 * クイズの答えを受け取りステータスを更新する
	 * どれかが範囲を超えていたらfalseを返す
	 */
	public boolean updateStatus(String ans){
		boolean flag = true;
		statusList[0]-=countMatchString(ans, "L");
		statusList[1]-=countMatchString(ans, "R");
		statusList[2]-=countMatchString(ans, "U");
		statusList[3]-=countMatchString(ans, "D");

		for (int i:statusList){
			if(i < 0){
				flag = false;
			}
		}

		return flag;
	}


	/** 引数1の文字列に引数2の文字がいくつ含まれているかを探索 */
	public int countMatchString(String str,String pattern){
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		int hitCount=0;
		while (m.find()){
			hitCount++;
		}

		return hitCount;
	}
}
