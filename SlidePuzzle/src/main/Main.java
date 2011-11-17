package main;

import algorithm.PuzzleFor15;
import puzzle.Puzzle;
import puzzle.QuizeManager;
import io.QuizeFileManager;

public class Main {
	public static void main(String args[]){
		/*---Puzzleの生成---*/
		QuizeFileManager qfm = new QuizeFileManager();
		QuizeManager qm = new QuizeManager(qfm.getNewLine().split(" "), qfm.getNewLine());
		String tmp_line=qfm.getNewLine();

		//全パズルを格納する
		while(tmp_line!=null){
			qm.setPuzzle(tmp_line);
			tmp_line=qfm.getNewLine();
		}

		//回答数の格納
		int count=0;

		String tmpAns="";
		/*---クイズを解く---*/
		Puzzle tmp_puzzle = qm.getNextPuzzle();
		while(tmp_puzzle != null){
				tmpAns="";
				//クイズの解法,クイズの大きさとかからalgorithmを選択する？
				PuzzleFor15 pf = null;
				try{
					 pf = new PuzzleFor15(tmp_puzzle);
				}catch(Exception e){

				}

				//クイズを解き終わった時の処理
				if(pf != null)	tmpAns = pf.getAnswer();

				System.out.println(tmpAns);
				if(qm.updateStatus(tmpAns) && (tmpAns!="")){
					qfm.writeAnswer(tmpAns);
					count++;
				}else{
					qfm.writeAnswer("");
				}
			tmp_puzzle = qm.getNextPuzzle();
		}
		System.out.println(count);
		//すべてのクイズを解き終わった
	}

}
