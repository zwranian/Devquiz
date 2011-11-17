package puzzle;


public class Puzzle {

	/** 横サイズ */
	int weight;

	/** 縦サイズ */
	int height;

	/** 問題 */
	char[] exercise;

	/** 空白の位置を保持した配列 */
	int space;

	/** 移動回数(方向)を保持する */
	String move_log;

	/** IDを格納する */
	private int id=0;

	public Puzzle(String line){
		String[] tmp = line.split(",");

		weight = Integer.valueOf(tmp[0]);
		height = Integer.valueOf(tmp[1]);

		exercise = new char[height*weight];

		space = -1;

		move_log="";


		//問題を格納
		for(int i=0;i<height*weight;i++){
			exercise[i] = tmp[2].charAt(i);
			if(exercise[i] == '0'){
				space=i;
			}
		}
	}


	public Puzzle(char[] exer,int h,int w,int s,String log){
		exercise = new char[h*w];
		for(int i=0;i<h*w;i++){
			exercise[i] = exer[i];
		}

		this.height = h;
		this.weight = w;
		space = s;

		this.move_log = log;

	}

	/** 空白を動かす,成功した場合はtrueを返す
	 * 0:left,1:right,2:up,3:down */
	public boolean moveSpace(int course){
		//移動先の格納変数
		int x=-1;
		boolean moved = true;
		String tmp="";

		switch (course) {
		case 0:
			//左移動
			//段の境目の場合は移動できない(左端の列)
			if(space%weight != 0){
				x = space -1;
				tmp="L";
				if(move_log.endsWith("R")) moved=false;
			}else{
				moved=false;
			}
			break;
		case 1:
			//右移動
			//右端の場合は移動できない
			if(space%weight != weight-1){
				x = space +1;
				tmp="R";
				if(move_log.endsWith("L")) moved=false;
			}else{
				moved=false;
			}
			break;
		case 2:
			//上移動
			if(space/weight >= 1){
				x = space - weight;
				tmp="U";
				if(move_log.endsWith("D")) moved=false;
			}else{
				moved=false;
			}
			break;
		case 3:
			//下移動
			if(space/weight < height-1){
				x = space + weight;
				tmp="D";
				if(move_log.endsWith("U")) moved=false;
			}else{
				moved=false;
			}
			break;
		}

		//移動先が存在するかチェック
		if( x > -1 && x < height*weight && moved){
			//壁でなければ
			if(exercise[x] != '='){
				exercise[space] = exercise[x];
				exercise[x]='0';
				space=x;
				moved=true;
				move_log+=tmp;
			}
		}

		return moved;
	}


	/** Puzzleの問題を返す */
	public char[] getPuzzle(){
		return exercise;
	}

	/** 空白の位置を返す */
	public int getSpaceList(){
		return space;
	}

	/**
	 * 移動のログを返す
	 */
	public String getLog(){
		return move_log;
	}

	public int getHeight(){
		return height;
	}

	public int getWeight(){
		return weight;
	}

	public int getID(){
		return id;
	}
}
