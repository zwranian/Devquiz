package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import puzzle.Puzzle;

public class PuzzleFor15 {


	/** 答えを格納する */
	private String answer;

	/** 正解の配列を格納 */
	private char[] collect;


	/** 今までのすべての盤面を記録していく */
	private HashSet<String> allExercise;

	/** 正解の盤面を記録する */
	private HashMap<String, Puzzle> allCollectExercise;

	/** 問題のリスト */
	private LinkedList<Puzzle> nextExer;

	/** 正解から求めた盤面が入ったリスト */
	private LinkedList<Puzzle> nextCollectExer;


	/** 縦 */
	private int h;

	/** 幅 */
	private int w;

	/** 正解した場合のフラグ */
	private boolean isCollect = false;

	/** 限界のフラグ */
//	private boolean isLimit = false;

	public PuzzleFor15(Puzzle puzzle){
		//初期化
		answer ="";

		h = puzzle.getHeight();
		w = puzzle.getWeight();

		allExercise = new HashSet<String>();
		allCollectExercise = new HashMap<String, Puzzle>();
		nextExer = new LinkedList<Puzzle>();
		nextCollectExer = new LinkedList<Puzzle>();


		//正解を生成
		collect = new char[h*w];
		char tmpcode='1';
		for(int i=0;i<h*w;i++){
			//この値が問題に存在するか確認する
			boolean exists = false;
			for(int k=0;k<w*h;k++){
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
		collect[h*w -1]='0';


		/*-------algorithmを使って解く-------*/
		//最初に4つに分ける
		Puzzle puzzle_l = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_r = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_u = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		Puzzle puzzle_d = new Puzzle(puzzle.getPuzzle(), puzzle.getHeight(), puzzle.getWeight(), puzzle.getSpaceList(), puzzle.getLog());
		//これらをそれぞれ移動させ,盤面をさらっていく
		//またすべてを統括するリストに放り込む
		allExercise.add(String.valueOf(puzzle.getPuzzle()));
		if(puzzle_l.moveSpace(0)){
			nextExer.offer(puzzle_l);
			allExercise.add(String.valueOf(puzzle_l.getPuzzle()));
		}
		if(puzzle_r.moveSpace(1)){
			nextExer.offer(puzzle_r);
			allExercise.add(String.valueOf(puzzle_r.getPuzzle()));
		}
		if(puzzle_u.moveSpace(2)){
			nextExer.offer(puzzle_u);
			allExercise.add(String.valueOf(puzzle_u.getPuzzle()));
		}
		if(puzzle_d.moveSpace(3)){
			nextExer.offer(puzzle_d);
			allExercise.add(String.valueOf(puzzle_d.getPuzzle()));
		}

		//正解からさかのぼる
		Puzzle puzzle_cl = new Puzzle(collect,h,w,h*w-1,"");
		Puzzle puzzle_cu = new Puzzle(collect,h,w,h*w-1,"");
		puzzle_cu.moveSpace(2);
		puzzle_cl.moveSpace(0);
		allCollectExercise.put(String.valueOf(puzzle_cu.getPuzzle()), puzzle_cu);
		allCollectExercise.put(String.valueOf(puzzle_cl.getPuzzle()), puzzle_cl);
		nextCollectExer.offer(puzzle_cu);
		nextCollectExer.offer(puzzle_cl);

		int limit=0;
		if(h*w < 19){
			limit=3200000;
		}else if(h*w < 26){
			limit=2900000;
		}else if(h*w < 31){
			limit=2400000;
		}else if(h*w < 37){
			limit=1400000;
		}

		//答えのチェック
		while(!isCollect){
			getNext();
			getNextRe();
			if(allExercise.size() > limit){
//				isLimit=true;
				isCollect=true;
			}
		}

	}


	/** 次の盤面を求める */
	public void getNext(){
		Puzzle tmp = nextExer.pop();
		Puzzle puzzle_l = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_r = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_u = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_d = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		if(puzzle_l.moveSpace(0)){
			if(!existsPuzzle(puzzle_l)){
					nextExer.offer(puzzle_l);
			}
		}
		if(puzzle_r.moveSpace(1)){
			if(!existsPuzzle(puzzle_r)){
					nextExer.offer(puzzle_r);
			}
		}
		if(puzzle_u.moveSpace(2)){
			if(!existsPuzzle(puzzle_u)){
					nextExer.offer(puzzle_u);
			}
		}
		if(puzzle_d.moveSpace(3)){
			if(!existsPuzzle(puzzle_d)){
					nextExer.offer(puzzle_d);
			}
		}
	}

	/** 正解の盤面から逆探索 */
	public void getNextRe(){
		Puzzle tmp = nextCollectExer.pop();
		Puzzle puzzle_l = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_r = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_u = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());
		Puzzle puzzle_d = new Puzzle(tmp.getPuzzle(), tmp.getHeight(), tmp.getWeight(), tmp.getSpaceList(), tmp.getLog());

		if(puzzle_l.moveSpace(0)){
			if(!existsCollectPuzzle(puzzle_l)){
					nextCollectExer.offer(puzzle_l);
			}
		}
		if(puzzle_r.moveSpace(1)){
			if(!existsCollectPuzzle(puzzle_r)){
					nextCollectExer.offer(puzzle_r);
			}
		}
		if(puzzle_u.moveSpace(2)){
			if(!existsCollectPuzzle(puzzle_u)){
					nextCollectExer.offer(puzzle_u);
			}
		}
		if(puzzle_d.moveSpace(3)){
			if(!existsCollectPuzzle(puzzle_d)){
					nextCollectExer.offer(puzzle_d);
			}
		}
	}

	/** 新しい盤面かどうかを調べて、新しいものならば次に進め、過去にあればここで打ち止めにする
	 * すでにあるときtrueを返し、ない場合はfalseをかえす*/
	public boolean existsPuzzle(Puzzle p)
	{
		boolean exists = true;
		//ハッシュで求める
		exists = allExercise.add(String.valueOf(p.getPuzzle()));
		if(exists){
			if(allCollectExercise.containsKey(String.valueOf(p.getPuzzle()))){
				isCollect = true;
				answer = p.getLog() + reverseLog(allCollectExercise.get(String.valueOf(p.getPuzzle())).getLog());
				System.out.println(w+":"+h+"size:"+allExercise.size());
			}
		}
		return !exists;
	}

	/** 逆順からのLogを反転させる */
	public String reverseLog(String rLog){
		String tmp="";

		String[] tmpAry = rLog.split("");
		for(int i=1;i<tmpAry.length+1;i++){
			if(tmpAry[tmpAry.length-i].equals("L")){
				tmp+="R";
			}else if(tmpAry[tmpAry.length-i].equals("R")){
				tmp+="L";
			}else if(tmpAry[tmpAry.length-i].equals("U")){
				tmp+="D";
			}else if(tmpAry[tmpAry.length-i].equals("D")){
				tmp+="U";
			}
		}
		return tmp;
	}


	/** すでにあるときtrue,無いときfalse */
	public boolean existsCollectPuzzle(Puzzle p)
	{
		boolean exists = false;
		exists = allCollectExercise.containsKey(String.valueOf(p.getPuzzle()));
		if(!exists){
			allCollectExercise.put(String.valueOf(p.getPuzzle()), p);
		}
		return exists;
	}

	public String getAnswer(){
		return answer;
	}

}
