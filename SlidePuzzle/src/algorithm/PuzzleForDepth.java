package algorithm;

import puzzle.Puzzle;

public class PuzzleForDepth {

	/** 答え */
	private String answer ="";


	/** 縦のサイズ */
	private int height;

	/** 横幅のサイズ */
	private int weight;


	/** 正解のフラグ */
	private boolean isCollect = false;

	/** 正解の配列 */
	private char[] collect;

	/** 深さ制限 */
	private int limit=0;


	public PuzzleForDepth(Puzzle puzzle){
		//初期化
		height = puzzle.getHeight();
		weight = puzzle.getWeight();
		collect = new char[weight*height];

		//正解を生成
		collect = new char[height*weight];
		char tmpcode='1';
		for(int i=0;i<height*weight;i++){
			//この値が問題に存在するか確認する
			boolean exists = false;
			for(int k=0;k<weight*height;k++){
				if(puzzle.getPuzzle()[k] == tmpcode){
					exists = true;
					break;
				} 
			}
			if(exists){
				collect[i]=tmpcode;
			}else{
				collect[i] = '=';
			}
			tmpcode++;
			if(tmpcode == 58){
				tmpcode = 'A';
			}
		}
		collect[height*weight -1]='0';

		/*----- アルゴリズムを使って解く -----*/
		//最初に4つに分ける
		Puzzle puzzle_l = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_r = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_u = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_d = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		

	}
	/** 制限を超えるか、行き止まりになるまで進み続ける */
	public void getNext(Puzzle p){

	}


	/** 深さ制限を超えていないかを確認する
	 * 超えたとき,false 超えていないときはtrue */
	public boolean isOver(int lenght){
		boolean tmp=true;
		if(lenght > limit) tmp = false;
		return tmp;
	}


	/** 答えを取得する */
	public String getAnswer(){
		return answer;
	}

}
