package main

import (
    "fmt"
    "io"
    "strings"
    hex "encoding/hex"
    "os"
    zlib "compress/zlib"
    "bytes"
    /* add more */
)

//グローバル変数で画像の大きさと深度を保持する
var weight int
var height int
var deep int
var color int
var count int

func CountColor(png io.Reader) int {
    /* modify here */
	//最初の識別部を読み込んで出力
	var tmp_buf [8]byte
	readString(png,tmp_buf[:])
	
	//ここからデータを読み込んでいく
	for i:=1;0<i; {
		i = splitChank(png)
//		fmt.Printf("読み込みデータ量:%d\n",i)
	}
	return count
}

/* 指定したバイト数を引数に与えたリーダーから読み込み16進数文字列として返す関数  */
func readString(r io.Reader,b []byte) string {
	io.ReadFull(r,b[:])
	return hex.EncodeToString(b[:])
}

/* 引数に与えた文字列をASCIIコードに基づいて変換する関数  */
func acsiiCode(b []byte) string {
//4byteを想定
	s := ""
	s = s + string(b[0])
	s = s + string(b[1])
	s = s + string(b[2])
	s = s + string(b[3])
	return s
} 

/* チャンク部を読み込み、各構造ごとに分ける関数 読み込んだバイト数を返す  */
func splitChank(r io.Reader) int{
	var dataLen [4]byte
	var chankName [4]byte
	var data [100000]byte
	var crc [4]byte	

	readSize := 0
	tmp := 0
	
	var e os.Error

	//データの長さを取得する
	tmp,e = io.ReadFull(r,dataLen[:])	
	//エラーチェック
	if e != nil {
		return -1
	}
	readSize = tmp
	//データ長を整数にして格納
	dLen := byteInt(dataLen[:])
	
	
	//チャンクの名称を読み込む
	tmp,e = io.ReadFull(r,chankName[:])
	//エラーチェック
	if e != nil {
		return -1
	}
	readSize = readSize + tmp
	//チャンクの名前を調べる、色数に関係する場合は適した処理を行う
	name := acsiiCode(chankName[:])
	
//	fmt.Println(name)	
	
	//データの本体を読み込む
	tmp,e = io.ReadFull(r,data[0:dLen])
	//エラーチェック
	if e != nil {
		return -1
	}	
	readSize = readSize + tmp
	
	//チャンクに適した処理を行う
	if name =="IHDR" {
		methodOfIHDR(data[:])
	}else if name =="IDAT" {
		methodOfIDAT(data[:])
	}else if name =="IEND" {
		return -1
	}
	

	//エラーチェックを読み込む
	io.ReadFull(r,crc[:])
	if e != nil {
		return -1
	}
	readSize = readSize + tmp

	return readSize
}
/* ビックエンディアンでbyteに格納されているデータを整数に変換する  */
func byteInt(b []byte) int{
//4byteの配列を想定,それ以外の場合はエラーが発生するはず
	//結果を格納する整数
	i := 0	
	i = i + int(b[3])
	i = i + ( int(b[2]) * 256)
	i = i + ( int(b[1]) * 65536)
	i = i + ( int(b[0]) * 16777216)
	return i
}

/* IHDRチャンクの構造を解析する関数  */
func methodOfIHDR(b []byte) {
	//イメージの幅4byte,高さ4byte,深さ1byte,カラータイプ1byte,圧縮方法1byte,フィルター方式1byte,インタレース1byte
//	fmt.Printf("幅:%d",int(byteInt(b[:4])))//幅
//	fmt.Printf("高さ:%d",int(byteInt(b[4:8])))//高さ
//	fmt.Printf("深度:%d",int(b[8]))//深さ
//	fmt.Printf("Color:%d \n",int(b[9]))//カラータイプ
//	fmt.Println(b[10:11])
//	fmt.Println(b[11:12])
//	fmt.Println(b[12:13])
	weight = byteInt(b[:4])
	height = byteInt(b[4:8])
	deep = int(b[8])
	color = int(b[9])
}

/* IDATチャンク構造を解析する関数  */
func methodOfIDAT(b []byte)  {
	buf := bytes.NewBuffer(b[:])
	r, err := zlib.NewReader(buf)
	if err != nil {
		fmt.Println(err)
	}

	//圧縮方式1byte,続いて1ピクセル(画素)あたりRGBアルファそれぞれ1byteで合計4byte
	//そして1スキャンラインは画素*幅で算出される
	var scanLine [100000]byte//スキャンラインを格納
	var lineSize int//ラインのサイズ
	if color == 6 {
		lineSize = 1 + (((4*deep)/8)*weight)
	}else if color == 2 {
		lineSize = 1 + (((3*deep)/8)*weight)
	}
	//マッチした色を格納する二次元配列
	rgb := make([][]uint8,1,1)
	rgb[0] = make([]uint8,3,3)
	
	var tmpLine [100000]byte
	for index:=0;index<height;index++ {
		io.ReadFull(r,scanLine[:lineSize])
		if color == 6 {
			pngDec6(int(scanLine[0]),tmpLine[1:lineSize],scanLine[1:lineSize],index)
			//同じ色があるかどうかの判定とカウント
			for j:=1;j<lineSize;j = j+4 {
				equal := 0
				for i:=0;i < len(rgb);i++ {
					if rgb[i][0] == scanLine[j] {
						if rgb[i][1] == scanLine[j+1] {
							if rgb[i][2] == scanLine[j+2] {
								equal = -1		
								break
							}
						}
					}
				}
				if equal ==  0 {
					tmp_rgb := []uint8{ scanLine[j],scanLine[j+1],scanLine[j+2] }
	//				fmt.Printf("-----------------\n")
	//				fmt.Println(j)
	//				fmt.Println(tmp_rgb)
	//				fmt.Printf("-----------------\n")
					rgb = append(rgb, tmp_rgb)
				}
			}
			copy(tmpLine[1:lineSize],scanLine[1:lineSize])
			for a:=5;a<lineSize;a=a+4 {
	//			fmt.Println(a-4)
	//			fmt.Println(scanLine[a-4:a])
			}
		}else if color == 2 {
			pngDec2(int(scanLine[0]),tmpLine[1:lineSize],scanLine[1:lineSize],index)
			//同じ色があるかどうかの判定とカウント
			for j:=1;j<lineSize;j = j+3 {
				equal := 0
				for i:=0;i < len(rgb);i++ {
					if rgb[i][0] == scanLine[j] {
						if rgb[i][1] == scanLine[j+1] {
							if rgb[i][2] == scanLine[j+2] {
								equal = -1		
								break
							}
						}
					}
				}
				if equal ==  0 {
					tmp_rgb := []uint8{ scanLine[j],scanLine[j+1],scanLine[j+2] }
					rgb = append(rgb, tmp_rgb)
				}
			}
			copy(tmpLine[1:lineSize],scanLine[1:lineSize])
		}
	}
		count = len(rgb)-1
}	 

/* 圧縮タイプを判別して生のデータに戻す簡単なお仕事  
	フィルターアルゴリズムはピクセル毎ではなく、バイト毎に演算を定義している
	rの値はrの値と比較する、この場合colorTypeが6である処理を実装(アルファ値を含む)*/
func pngDec6(id int,ob []byte ,b []byte, index int) []byte{
	var i int
	switch id {
	case 0:
		return b[:]
	case 1:
		//subフィルタ処理
		for i = 4;i<len(b);i=i+4 {
			b[i] = b[i-4] + b[i]
			b[i+1] = b[i-3] + b[i+1]
			b[i+2] = b[i-2] + b[i+2]
			b[i+3] = b[i-1] + b[i+3]	
		}
		return b[:]
	case 2:
		//Up処理
		//前の列がない場合はそのまま返す
		if index == 0 {
			return b[:]
		}
		
		for i=0;i<len(b);i++ {
			b[i]   = ob[i] + b[i]
		}
		return b[:]
	case 3:
		//Averageフィルタ処理
		if index == 0 {
			for i=4;i<len(b);i++ {
				b[i] = (b[i-4]/2) + b[i]
			}
		}else {
			b[0] = ob[0]/2 + b[0]
			b[1] = ob[1]/2 + b[1]
			b[2] = ob[2]/2 + b[2]
			b[3] = ob[3]/2 + b[3]
				
			for i=4;i<len(b);i++ {
				b[i]   = (  (( b[i-4] + ob[i] )/2) + b[i])
			}
		}
		return b[:]
	case 4:
		//Paethフィルタ処理
		var s int
		if index == 0 {
                        for i = 4;i<len(b);i++ {
                                        s = paeth(int(b[i-4]),0,0)
                                        if s == 1 {
                                                b[i] = b[i-4] + b[i]
                                        }
                        }	
			
		}else {
		//かならず2が帰ってくる
			for i=0;i<4;i++ {
				s = paeth(0,int(ob[i]),0)
				if s==2 {
					b[i] = ob[i]+b[i]
				}
			}

			for i = 4;i<len(b);i++ {
					s = paeth(int(b[i-4]),int(ob[i]),int(ob[i-4]))
					if s == 1 {
						b[i] = b[i-4] + b[i]
					}else if s == 2 {
						b[i] = ob[i] + b[i]
					}else if s == 3 {
						b[i] = ob[i-4] + b[i]
					}
			}
		}
		return b[:]
	}
	return nil	
}
/** フィルターアルゴリズムでデコードする
	colortypeが2である、アルファ値が含まれない場合の処理 */
func pngDec2(id int ,ob []byte ,b []byte, index int) []byte {
	var i int
	switch id {
	case 0:
		return b[:]
	case 1:
		//subフィルタ処理
		for i = 3;i<len(b);i=i+3 {
			b[i] = b[i-3] + b[i]
			b[i+1] = b[i-2] + b[i+1]
			b[i+2] = b[i-1] + b[i+2]
		}
		return b[:]
	case 2:
		//Up処理
		//前の列がない場合はそのまま返す
		if index == 0 {
			return b[:]
		}
		
		for i=0;i<len(b);i++ {
			b[i]   = ob[i] + b[i]
		}
		return b[:]
	case 3:
		//Averageフィルタ処理
		if index == 0 {
			for i=3;i<len(b);i++ {
				b[i] = (b[i-3]/2) + b[i]
			}
		}else {
			b[0] = ob[0]/2 + b[0]
			b[1] = ob[1]/2 + b[1]
			b[2] = ob[2]/2 + b[2]
				
			for i=3;i<len(b);i++ {
				b[i]   = (  (( b[i-3] + ob[i] )/2) + b[i])
			}
		}
		return b[:]
	case 4:
		//Paethフィルタ処理
		var s int
		if index == 0 {
                        for i = 3;i<len(b);i++ {
                                        s = paeth(int(b[i-3]),0,0)
                                        if s == 1 {
                                                b[i] = b[i-3] + b[i]
                                        }
                        }	
			
		}else {
		//かならず2が帰ってくる
			for i=0;i<3;i++ {
				s = paeth(0,int(ob[i]),0)
				if s==2 {
					b[i] = ob[i]+b[i]
				}
			}

			for i = 3;i<len(b);i++ {
					s = paeth(int(b[i-3]),int(ob[i]),int(ob[i-3]))
					if s == 1 {
						b[i] = b[i-3] + b[i]
					}else if s == 2 {
						b[i] = ob[i] + b[i]
					}else if s == 3 {
						b[i] = ob[i-3] + b[i]
					}
			}
		}
		return b[:]
	}
	return nil	
}

/* 圧縮タイプ4のPaethフィルタの計算式を行う関数
	返値として左:1,上:2,左上:3とする  */
func paeth( l int, u int ,lu int) int{
	x := l + u - lu

	a := x - l
	b := x - u
	c := x - lu
	if a < 0 {
		a = a *-1
	}
	if b < 0 {
		b = b *-1
	}
	if c < 0 {
		c = c *-1
	}
	
	switch {
	case a<=b && a<=c:
		return 1
	case b<a && b<=c:
		return 2
	case c<a && c<b:
		return 3
	}
	return -1
}

/* これらの関数は提出時に自動挿入されます。 */
func main() {
    png := GetPngBinary()
    cnt := CountColor(png)
    fmt.Println(cnt)
}

func GetPngBinary() io.Reader {
    // img_strの中身は提出するたびに変化します。
    img_str := "\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x18\x00\x00\x00\x08\x08\x06\x00\x00\x00\xe3\xa1?c\x00\x00\x02\xeeiCCPICC Profile\x00\x00x\x01\x85T\xcfk\x13A\x14\xfe6n\xa9\xd0\"\x08Zk\x0e\xb2x\x90\"IY\xabhE\xd46\xfd\x11bk\x0c\xdb\x1f\xb6E\x90d3I\xd6n6\xeb\xee&\xb5\xa5\x88\xe4\xe2\xd1*\xdeE\xed\xa1\x07\xff\x80\x1ez\xf0d/J\x85ZE(\xde\xab(b\xa1\x17-\xf1\xcdnL\xb6\xa5\xea\xc0\xce~\xf3\xde7\xef}ov\xdf\x00\rr\xd24\xf5\x80\x04\xe4\r\xc7R\xa2\x11il|Bj\xfc\x88\x00\x8e\xa2\tA4%U\xdb\xecN$\x06A\x83s\xf9{\xe7\xd8z\x0f\x81[V\xc3{\xfbw\xb2w\xad\x9a\xd2\xb6\x9a\x07\x84\xfd@\xe0G\x9a\xd9*\xb0\xef\x17q\nY\x12\x02\x88<\xdf\xa1)\xc7t\x08\xdf\xe3\xd8\xf2\xec\x8f9Nyx\xc1\xb5\x0f+=\xc4Y\"|@5-\xce\x7fM\xb8S\xcd%\xd3@\x83H8\x94\xf5qR>\x9c\xd7\x8b\x94\xd7\x1d\x07inf\xc6\xc8\x10\xbdO\x90\xa6\xbb\xcc\xee\xabb\xa1\x9cN\xf6\x0e\x90\xbd\x9d\xf4~N\xb3\xde>\xc2!\xc2\x0b\x19\xad?F\xb8\x8d\x9e\xf5\x8c\xd5?\xe2a\xe1\xa4\xe6\xc4\x86=\x1c\x185\xf4\xf8`\x15\xb7\x1a\xa9\xf85\xc2\x14_\x10M'\xa2Tq\xd9.\r\xf1\x98\xae\xfdV\xf2J\x82p\x908\xcada\x80sZHO\xd7Ln\xf8\xba\x87\x05}&\xd7\x13\xaf\xe2wVQ\xe1y\x8f\x13g\xde\xd4\xdd\xefE\xda\x02\xaf0\x0e\x1d\x0c\x1a\x0c\x9a\rHP\x10E\x04a\x98\xb0P@\x86<\x1a14\xb2r?#\xab\x06\x1b\x93{2u$j\xbbtbD\xb1A{6\xdc=\xb7Q\xa4\xdd<\xfe(\"q\x94C\xb5\x08\x92\xfcA\xfe*\xaf\xc9O\xe5y\xf9\xcb\\\xb0\xd8V\xf7\x94\xad\x9b\x9a\xba\xf2\xe0;\xc5\xe5\x99\xb9\x1a\x1e\xd7\xd3\xc8\xe3sM^|\x95\xd4v\x93WG\x96\xacyz\xbc\x9a\xec\x1a?\xecW\x971\xe6\x825\x8f\xc4s\xb0\xfb\xf1-_\x95\xcc\x97)\x8c\x14\xc5\xe3U\xf3\xeaK\x84uZ17\xdf\x9fl\x7f;=\xe2.\xcf.\xb5\xd6s\xad\x89\x8b7V\x9b\x97g\xfdjH\xfb\xee\xaa\xbc\x93\xe6U\xf9O^\xf5\xf1\xfcg\xcd\xc4c\xe2)1&v\x8a\xe7!\x89\x97\xc5.\xf1\x92\xd8K\xab\x0b\xe2`m\xc7\x08\x9d\x95\x86)\xd2m\x91\xfa$\xd5``\x9a\xbc\xf5/]?[x\xbdF\x7f\x0c\xf5Q\x94\x19\xcc\xd2T\x89\xf7\x7f\xc2*d4\x9d\xb9\x0eo\xfa\x8f\xdb\xc7\xfc\x17\xe4\xf7\x8a\xe7\x9f(\x02/l\xe0\xc8\x99\xbamSq\xef\x10\xa1e\xa5ns\xae\x02\x17\xbf\xd1}\xf0\xb6nk\xa3~8\xfc\x04X<\xab\x16\xadR5\x9f \xbc\x01\x1cv\x87z\x1e\xe8)\x98\xd3\x96\x96\xcd9R\x87,\x9f\x93\xba\xe9\xcabR\xccP\xdbCRR\xd7%\xd7eK\x16\xb3\x99Ub\xe9v\xd8\x99\xd3\x1dn\x1c\xa19B\xf7\xc4\xa7Je\x93\xfa\xaf\xf1\x11\xb0\xfd\xb0R\xf9\xf9\xacR\xd9~N\x1a\xd6\x81\x97\xfao\xc0\xbc\xfdE\xc0x\x8b\x89\x00\x00\x00\x97IDAT(\x15\x9d\x92\x81\x0e\x80 \x08D\xa5\xf9m\xf5Y\xad\xcf\xaa\x9f#nz$\xba\xb9\xca\xad\x85\xc1\xbd\x03S\xd4\x96H\xf2\xa5\xea\xe1\xeb@\x8e\x02\xd0}\x14/\x80\x03\xca\xa75{\xeb0\x80\x01\xa9\xa0\xdcC|\x02:\xf1\xc3U\xc7\\K\x97}\xda9HPcq0pQ\x8aE\xe94y\x05'3\x92M[\x86\xc7\xc1\xa4n\x82\x01\x8ci\xe2\xc5\x7f\x02N`\xda\xdcCK\xaeqbqsD\xbd\x86=\xe0g\xdb\x9dy\xba\xb4Xp\x8bX\xf0\xe7\x8d\x89g\x84pD_\x0cx\x9438x7\xa4yC\r7\x04z\xae\x00\x00\x00\x00IEND\xaeB`\x82"
    return strings.NewReader(img_str)
}

