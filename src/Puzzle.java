package Depthfirstsearch;


import java.io.*;
import java.util.ArrayList;

public class Puzzle;
{
public static final int N = 3 ;  //  assumes 3x3x3 solution ciube
public static final int P = 7 ;  // assumes 7 pieces in puzzle
private static int solutionCube;
private static int puzzlePieces;

public static int[][][] mapTable ;
public static int[] xInvTable ;
public static int[] yInvTable ;
public static int[] zInvTable ;

public static piece3D[] pieces ;

public static int[] solution ;
public static boolean continuing ;

public static char[][][] solutionCharMatrix ;


//=====================================================================================================


public static void outputSolution( )
{
  //--- fill 3x3x3 char matrix with piece ID's per solution piece placements decoded from solution bitmaps
 
    for ( int whichPiece = 0 ; whichPiece < 7 ; whichPiece++ )
      {
        char idChar = pieces[whichPiece].pieceID ;
        int integratedBitmap = solution[whichPiece] ;
        for ( int i = 0 ; i < N*N*N ; i++ )
          {
            if ( integratedBitmap % 2 == 1 )
              solutionCharMatrix[xInvTable[i]][yInvTable[i]][zInvTable[i]] = idChar ;
            integratedBitmap >>= 1 ;                         
          }
      }

  //--- output 3x3x3 char matrix showing pieceID's at each location, layer-by-layer in z- direction   
     

    System.out.println( ) ;
    System.out.println( "EACH CELL OF THE 3x3x3 SOLUTION CUBE IS LABELED WITH THE CHAR PIECE-ID OF THE PIECE" ) ;
    System.out.println( "TO WHICH THE UNIT CUBE OCCUPYING THAT CELL BELONGS (OUTPUT LAYER-BY-LAYER IN Z-ORDER)" ) ;
    System.out.println( ) ;
   
    for ( int z = 2 ; z >= 0 ; z-- )
      {
        System.out.println( ) ;
        System.out.println( "z = " + z + " layer" ) ;
        System.out.println( ) ;
        for ( int x = 2 ; x >= 0 ; x-- )
          {
            for ( int y = 2 ; y >= 0 ; y-- )
              System.out.print( solutionCharMatrix[x][y][z] ) ;
            System.out.println( ) ;
          }           
      }     

}


//=====================================================================================================


public static void DFS ( int level , int priorPartialSolution )
{
  //--- recursive depth-first search to search for solution among all possible piece placement combinations
   
    if ( level == P )
        {
           outputSolution( ) ;
           continuing = false ;     
        }
      else
        {
          bitmapNode ptr = pieces[level].possiblePositionsBitmaps ;
          while ( (ptr != null) && continuing )
            {
              int overlap = priorPartialSolution & ptr.bitmap ;
              if ( overlap == 0 ) // no overlap
                {
                  solution[level] = ptr.bitmap ;
                  DFS ( level+1 , priorPartialSolution | ptr.bitmap ) ;
                }
              ptr = ptr.link ;
            }
        }
}


//=====================================================================================================


@SuppressWarnings("resource")
public static void main(String[] args )
{
    //change filename here to use for program!
    String fileName ="cubes.txt";
    String line;
    int pieceCounted = 0;
    int z = 0;
    int ptr = 0;
   
    ArrayList<Integer> xCoor = new ArrayList<Integer>();
    ArrayList<Integer> yCoor = new ArrayList<Integer>();
    ArrayList<Integer> zCoor = new ArrayList<Integer>();
    ArrayList<Integer> pieceIDs = new ArrayList<Integer>();
    ArrayList<Integer> pieceCount = new ArrayList<Integer>();
    char[] letters = {'A','B','C','D','E','F','G','H'};
   
   
    try{   
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        //get N & P
        solutionCube = Integer.parseInt(in.readLine());
        puzzlePieces = Integer.parseInt(in.readLine());
       
        while(in != null && pieceCounted != puzzlePieces){
            //first line is always piece ID
                pieceIDs.add(Integer.parseInt(line = in.readLine()));
           
            //second line is always number of pieces
            pieceCount.add(Integer.parseInt(line = in.readLine()));
           
            for(int i = 0; i < pieceCount.get(pieceCounted); i++){
                line = in.readLine();
                String[] values = line.split("\\s+");
                  xCoor.add(Integer.parseInt(values[0]));
                yCoor.add(Integer.parseInt(values[1]));
                zCoor.add(Integer.parseInt(values[2]));
            }
            pieceCounted++;
        }
    }
    catch(Exception e){
        e.printStackTrace();
    }
   
    //for each piece ID
    for(Integer c: pieceIDs){
        System.out.println(c);
        for(int i = 0; i < pieceCount.get(c-1) ; i++){
            System.out.println(xCoor.get(z) + " " + yCoor.get(z) + " " + zCoor.get(z) );
            z++;
        }
       
    }
   
 
  //--- instantiate and compute table for forward and co-inverse bitmap mapping(s)
 
    mapTable  = new int[N][N][N] ;
    xInvTable = new int[N*N*N] ;
    yInvTable = new int[N*N*N] ;
    zInvTable = new int[N*N*N] ;
    int count = 0 ;
    for ( int ix = 0 ; ix < N ; ix++ )
      for ( int iy = 0 ; iy < N ; iy++ )
        for ( int iz = 0 ; iz < N ; iz++ )
          {
            mapTable[ix][iy][iz] = 1 << count ;
            xInvTable[count] = ix ;
            yInvTable[count] = iy ;
            zInvTable[count] = iz ;
            count++ ;
          }

  //--- instantiate solution bitmap vector and solution character matrix 
 
//    solution = new int[7] ;
//    solutionCharMatrix = new char[N][N][N] ; 
    solution = new int[puzzlePieces];
    solutionCharMatrix = new char[solutionCube][solutionCube][solutionCube];
       
  //--- instantiate the pieces comprising the puzzle, as an array of piece3D objects indexed from 0    
  // generalize so that input can be changed when setting cubes     
   // pieces = new piece3D[7] ; // for this hint, the puzzle is assumed to use 7 pieces
    pieces = new piece3D[puzzlePieces];

   
    for(int j = 0; j < puzzlePieces; j++){
        //System.out.println(j + " " + letters[j] + " " + pieceCount.get(j));
        pieces[j] = new piece3D( letters[j] , pieceCount.get(j) ) ;
        for(int k = 0; k < pieceCount.get(j); k++){
            //System.out.println("pieceCount is " + pieceCount.get(j));
            //System.out.println("k = " + k +  " X = " + xCoor.get(ptr)+ " Y = " + yCoor.get(ptr) + " Z = " + zCoor.get(ptr));
            pieces[j].setCube(k, xCoor.get(ptr),yCoor.get(ptr), zCoor.get(ptr)) ;
            //System.out.println("ptr is... " + ptr);
            ptr++;
        }
          // unit cubes comprising a piece are indexed from 0F
        
        
    }
    
    
//    pieces[0] = new piece3D( 'A' , 4 ) ;
//    pieces[0].setCube(0,0,0,0) ;  // unit cubes comprising a piece are indexed from 0
//    pieces[0].setCube(1,1,0,0) ;
//    pieces[0].setCube(2,2,0,0) ;
//    pieces[0].setCube(3,0,0,1) ;
// 
//    pieces[1] = new piece3D( 'B' , 4 ) ;
//    pieces[1].setCube(0,0,0,0) ;
//    pieces[1].setCube(1,0,1,0) ;
//    pieces[1].setCube(2,0,1,1) ;
//    pieces[1].setCube(3,0,2,1) ;
// 
//    pieces[2] = new piece3D( 'C' , 3 ) ;
//    pieces[2].setCube(0,0,0,0) ;
//    pieces[2].setCube(1,0,0,1) ;
//    pieces[2].setCube(2,1,0,0) ;
// 
//    pieces[3] = new piece3D( 'D' , 5 ) ;
//    pieces[3].setCube(0,0,0,0) ;
//    pieces[3].setCube(1,0,0,1) ;
//    pieces[3].setCube(2,1,0,1) ;
//    pieces[3].setCube(3,0,1,1) ;
//    pieces[3].setCube(4,1,1,1) ;
// 
//    pieces[4] = new piece3D( 'E' , 5 ) ;
//    pieces[4].setCube(0,0,0,0) ;
//    pieces[4].setCube(1,0,1,0) ;
//    pieces[4].setCube(2,1,0,0) ;
//    pieces[4].setCube(3,0,1,1) ;
//    pieces[4].setCube(4,1,0,1) ;
// 
//    pieces[5] = new piece3D( 'F' , 3 ) ;
//    pieces[5].setCube(0,0,0,0) ;
//    pieces[5].setCube(1,1,0,0) ;
//    pieces[5].setCube(2,2,0,0) ;
// 
//    pieces[6] = new piece3D( 'G' , 3 ) ;
//    pieces[6].setCube(0,0,0,0) ;
//    pieces[6].setCube(1,0,1,0) ;
//    pieces[6].setCube(2,0,2,0) ;

  //--- compute lists of possible positions for each piece
 
    for ( int whichPiece = 0 ; whichPiece < 7 ; whichPiece++ )
      pieces[whichPiece].findAllPossiblePositionsAsBitmaps( ) ;
   
  //--- DFS to examine all combinations of piece placement for each of seven pieeces to find solution
 
    continuing = true ;
    DFS ( 0 , 0 ) ;
 
}


}
