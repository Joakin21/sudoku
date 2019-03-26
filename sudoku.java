import java.awt.*;                                                    //                              
import java.awt.event.*;                                              //                              
import javax.swing.*;                                                 //                              
import java.io.*;
//Mis importaciones
import java.util.ArrayList;
import java.util.List;     

class Inmodificable{//Valores del tablero que no se pueden modificar (fila, columna)
    int fila;
    int columna;
    
    public Inmodificable(int fila, int columna){
        this.fila = fila;
        this.columna = columna;
    }
 
}                                                //                              
                                                                      //                              
public class sudoku extends JFrame implements ActionListener {        //                              
    Button    btnNuevo;                                               // boton para  mostrar un sudoku
    Button    btnSolu;                                                // boton para  solucionar sudoku
    Button    dig[]   = new Button[9];                                // botones  para  digitos 1 al 9
    Button    cas[][] = new Button[9][9];                             // botones  de celdas del sudoku
    int       a       = 40;                                           // ancho  boton usado como celda
    int       inow    = -1;                                           // indices boton  pulsado actual
    int       jnow    = -1;                                           //                              
    int       iant    = -1;                                           // indices del  pulsado anterior
    int       jant    = -1;                                           //                              
    int       bi[]    = { 0,0,0,1,1,1,2,2,2,0,0,0,1,1,1,2,2,2,0,0,0,  // fila de cada  boton  en orden
                          1,1,1,2,2,2,3,3,3,4,4,4,5,5,5,3,3,3,4,4,4,  // de  recorrido  por cuadricula
                          5,5,5,3,3,3,4,4,4,5,5,5,6,6,6,7,7,7,8,8,8,  // de  3x3  botones en  cada una
                          6,6,6,7,7,7,8,8,8,6,6,6,7,7,7,8,8,8 };      //                              
    int       bj[]    = { 0,1,2,0,1,2,0,1,2,3,4,5,3,4,5,3,4,5,6,7,8,  // columna  de cada  boton orden
                          6,7,8,6,7,8,0,1,2,0,1,2,0,1,2,3,4,5,3,4,5,  // de  recorrido  por cuadricula
                          3,4,5,6,7,8,6,7,8,6,7,8,0,1,2,0,1,2,0,1,2,  //                              
                          3,4,5,3,4,5,3,4,5,6,7,8,6,7,8,6,7,8 };      //                              
                                                                      //                              
    char      v[][]   = new char[100][81];                            // los valores para  100 sudokus
    int      r[][]   = new int[9][9];                            // un arreglo respaldo  de v[][]
    
    //Mis variables
    List<Inmodificable> inmodificables = new ArrayList<>();
                                                                      //                              
    public sudoku() {                                                 //                              
        setLayout(null);                                              // se  maneja posicion elementos
        setSize(415,540);                                             //                              
        getContentPane().setBackground(Color.black);                  //                              
                                                                      //                              
        int x=10, y=10;                                               // x, y iniciales de la interfaz
        btnNuevo = new Button("Nuevo Juego");                         // boton para crear nuevo sudoku
        btnNuevo.setBounds(x,y,3*a,a);                                //                              
        btnNuevo.setBackground(Color.cyan);                           //                              
        btnNuevo.setActionCommand("100");                             // le asigna  identificador  100
        btnNuevo.addActionListener(this);                             // agrega "escuchador" de  Clics
        getContentPane().add(btnNuevo);                               //                              
                                                                      //                              
        btnSolu = new Button("Solucion");                             // boton para  solucionar sudoku
        btnSolu.setBounds(6*a+30,y,0,0);                              //                              
        btnSolu.setBackground(Color.cyan);                            //                              
        btnSolu.setActionCommand("101");                              // le asigna  identificador  101
        btnSolu.addActionListener(this);                              // agrega "escuchador" de  Clics
        getContentPane().add(btnSolu);                                //                              
        y = y + a + 10;                                               // espacio vertical antes de que
                                                                      // de colocar botones del sudoku
        for(int i=0; i<9; i++) {                                      // coloca 81 botones  del sudoku
            x = 10;                                                   //                              
            for(int j=0; j<9; j++) {                                  //                              
                cas[i][j] = new Button();                             //                              
                cas[i][j].setBounds(x, y, a, a);                      //                              
                cas[i][j].setBackground(Color.cyan);                  // celeste  como  color de fondo
                cas[i][j].setEnabled(false);                          //                              
                cas[i][j].addActionListener(this);                    // agrega "escuchador" de  Clics
                getContentPane().add(cas[i][j]);                      // agrega el boton a la interfaz
                x = x + a;                                            // avanza la x un ancho de boton
                if((j+1)%3==0) x = x + 10;                            // cada 3 botones crea espaciado
            }                                                         //                              
            y = y + a;                                                // avanza la y un ancho de boton
            if((i+1)%3==0) y = y + 10;                                // cada 3 botones crea espaciado
        }                                                             //                              
                                                                      //                              
        x = 10;                                                       //                              
        for(int i=0; i<9; i++) {                                      // diez botones con digitos 1..9
            dig[i] = new Button(String.valueOf(i+1));                 // coloca digitos en  cada boton
            dig[i].setBounds(x, y, 0, 0);                             // inicia  boton sin dimensiones
            dig[i].setActionCommand(String.valueOf(90+i));            // les asigna identidad 90... 99
            dig[i].addActionListener(this);                           // agrega "escuchador" de  Clics
            getContentPane().add(dig[i]);                             // agrega el boton a la interfaz
            x = x + a;                                                // avanza la x un ancho de boton
            if((i+1)%3==0) x = x + 10;                                // cada 3 botones crea espaciado
        }                                                             //                              
                                                                      //                              
        File archivo = null;                                          // lee los  datos de 100 sudokus
        FileReader fr = null;                                         //                              
        BufferedReader br = null;                                     //                              
        try {                                                         //                              
            archivo = new File ("datos.txt");                         //                              
            fr = new FileReader (archivo);                            //                              
            br = new BufferedReader(fr);                              //                              
            String linea;                                             //                              
            int    n = -1;                                            //                              
            while((linea=br.readLine())!=null) {                      //                              
                n++;                                                  //                              
                for(int i=0;i<81;i++) {                               //                              
                    v[n][i] = linea.charAt(i);                        //                              
                }                                                     //                              
            }                                                         //                              
            fr.close();                                               //                              
        }                                                             //                              
        catch(Exception e){ e.printStackTrace(); }                    //                              
    }                                                                 //                              
                                                                      //                              
                                                                      //                              
   public void actionPerformed(ActionEvent e) {                       // METODO  SE  ACTIVA CON CLIC's
      int cmd=Integer.parseInt(e.getActionCommand());                 //                              
      Object boton = e.getSource();                                   //                              
      int i, j, f, c, ff, cc, d, da, m;                               //                              
      
      //                              
      if(boton==btnNuevo) {                                           // *** si Clic en boton btnNuevo
         for(i=0; i<9; i++) {                                         // recorre 81  botones de sudoku
            for(j=0; j<9; j++) {                                      //                              
               cas[i][j].setLabel("");                                // limpia  texto  de  este boton
               cas[i][j].setBackground(Color.cyan);                   // repone  color de fondo normal
               cas[i][j].setActionCommand(String.valueOf(9*i+j));     // da un valor de identificacion
               cas[i][j].setEnabled(false);                           //                              
            }                                                         //                              
         }                                                            //                              
                                                                      //  
         //AQUI WN !!!!!!!
         inmodificables.clear();
         m = (int)(Math.random()*100);                                // Selecciona  al azar un sudoku
         c = 0;                                                       // Indice  para  recorrer  v[][]
         for(i=0;i<9;i++) {                                           // recorre 81 botones del sudoku
             for(j=0;j<9;j++) {                                       //                              
                 r[i][j] = Character.getNumericValue(v[m][c]);                                   // copia  digito  en el respaldo
                 if(v[m][c]>48) {                                     // Si el digito actual  no  es 0
                     cas[i][j].setLabel(v[m][c]+"");                  // muestra digito  en este boton
                     cas[i][j].setBackground(Color.yellow);           // le asigna  un  color especial
                 }                                                    //                              
                 else cas[i][j].setEnabled(true);                     // si  es 0 habilita  este boton
                 c++;                                                 //                              
             }                                                        //                              
         }   
          
         
         asignarInmodificables();
         
         btnSolu.setSize(3*a, a);                                     // muestra  boton  para solucion
         for(int k=0; k<9; k++) dig[k].setSize(a, a);                 // muestra  botones  con digitos
         inow = -1;                                                   // fila,columna de boton pulsado
         jnow = -1;                                                   // -1 pues aun  no se ha pulsado
      }                                                               //                              
                                                                      //                              
      if(boton==btnSolu) {                                            // Si Clic en  el  boton btnSolu
         btnSolu.setSize(0,0);                                        // oculta  el  boton de solucion
         for(int k=0; k<9; k++) dig[k].setSize(0, 0);                 // oculta  botones  con  digitos
         
         //System.out.println();
         solucion(0,0);                                                           // 
      }                                                               //                              
                                                                      //                              
      if(cmd>-1 && cmd<80) {                                          // si Clic  en celda  del sudoku
         if(inow<0) {                                                 // si  no habia  boton  activado
            iant = cmd / 9;                                           // actual  es  tambien  anterior
            jant = cmd % 9;                                           //                              
         }                                                            //                              
         inow = cmd / 9;                                              // obtiene indices  boton actual
         jnow = cmd % 9;                                              // a  partir  del  Id  que posee
         r[inow][jnow]='0';                                           // elimina  actual  valor  boton
         cas[inow][jnow].setLabel("");                                //                              
         cas[iant][jant].setBackground(Color.cyan);                   // repone  color  boton anterior
         cas[inow][jnow].setBackground(Color.orange);                 // cambia color  de boton actual
         iant = inow;                                                 // y memoriza  su fila y columna
         jant = jnow;                                                 //                              
         pintaDigitos();                                              // colorea botones  1..9 validos
      }                                                               //                              
                                                                      //                              
      if(cmd>89 && cmd<100) {                                         // *** si Clic en  botones 0...9
         if(inow>-1) {                                                // solo si hay  un  boton activo
            char digito = dig[cmd-90].getLabel().charAt(0);           // obtiene el  digito del  boton
            if(sinConflicto(inow, jnow, digito)) {                    // si  ese digito  es uno valido
               cas[inow][jnow].setLabel(digito+"");                   // lo  muestra  en  boton actual
               cas[inow][jnow].setBackground(Color.cyan);             // repone  el  color  del  boton
               r[inow][jnow] = digito;                                // y escribe ademas en  respaldo
               pintaDigitos();                                        // actualiza  digitos permitidos
               inow = -1;                                             // coloca  estado "no seleccion"
            }                                                         //                              
        }                                                             //                              
      }                                                               //                              
   }                                                                  //                              
                                                                      //                              
   private void pintaDigitos() {                                      // COLOREA LOS  DIGITOS  VALIDOS
      int d, f, c;                                                    //                              
      for(c=0;c<9;c++) dig[c].setBackground(Color.orange);            // color  naranja  botones  1..9
      f = inow - inow%3;                                              // primera fila  cuadricula  3x3
      c = jnow - jnow%3;                                              // y primera  columna cuadricula
      for(int i=f;i<f+3;i++) {                                        // recorre para  identificar que
         for(int j=c;j<c+3;j++) {                                     // digitos  ya  existen  en ella
            if(r[i][j]>'0') {                                         // si aqui  ya  existe un digito
               d = (int)(r[i][j])-48;                                 // toma su  valor y lo  usa para
               dig[d-1].setBackground(Color.cyan);                    // reponer  el  color a su boton
            }                                                         //                              
         }                                                            //                              
      }                                                               //                              
   }                                                                  //                              
                                                                      //                              
   public boolean sinConflicto(int i, int j, char d) {                // VERIFICA SI DIGITO  ES VALIDO
      String ds = String.valueOf(d);                                  //                              
      int f0, c0;                                                     //                              
      for(int k=0; k<9; k++)                                          //                              
         if(cas[i][k].getLabel().equals(ds)) return false;            //                              
      for(int k=0; k<9; k++)                                          //                              
         if(cas[k][j].getLabel().equals(ds)) return false;            //                              
      f0 = i - i%3;                                                   // primera fila  cuadricula  3x3
      c0 = j - j%3;                                                   // y primera  columna cuadricula
      for(int f=f0;f<f0+3;f++)                                        // recorre la  cuadricula de 3x3
         for(int c=c0;c<c0+3;c++)                                     //                              
            if(r[f][c]==d) return false;                              // no toma valor de  celda  nula
      return true;                                                    //                              
   }                                                                  //                              
                                                                      //                              
                                                                      //                              
   private boolean sudo(int n) {                                      // SOLUCION RECURSIVA DEL SUDOKU
      return true;                                                // devuelve  resultado instancia
   }                                                                  //                              
                                                                      //                              
   public static void main() {                                        // METODO  PRINCIPAL DE LA CLASE
       sudoku sd = new sudoku();                                      // Instancia  esta  clase sudoku
       sd.setVisible(true);                                           // y cambia visibilidad  de ella
   }     
   
   //my code here    
   public void solucion(int f, int c){
        if(f == 9 && c == 0)//Ya paso la ultima casilla
        {
            //System.out.println("Nani wtf");
            for(int i=0;i<9;i++) {                                        // muestra  digitos  de solucion
             for(int j=0;j<9;j++) {                                     // en los  botones   del  sudoku
                  cas[i][j].setLabel(Integer.toString(r[i][j]));              //                              
             }                                                      //                              
         }
        }
        else
        {
            //System.out.println("kiuuuu :9");
            while(!esModificable(f,c)){
                //Nos movemos a la derecha
                if(c < 8)
                    c = c+1;
                else{
                    c = 0;
                    f = f+1;  
                }
            }
            if(f==9 && c==0 ){
                solucion(f, c);
            }
            else{
                for(int i=1 ; i<=9 ; i++){
                    
                    if( unicoEnFilaColumna(f, c, i) && unicoEnSuConjunto(f, c, i)){
    
                        agregar(f, c, i);
                        int fi = f;
                        int ci = c;
                        //Nos movemos a la derecha      
                        if(c < 8)
                            c = c+1;
                        else{
                            c = 0;
                            f = f+1;  
                        }
    
                        solucion(f, c); 
                        f = fi;
                        c = ci;
    
                    }
                    quitar(f, c);
                }
            }
        }
   }
   public void asignarInmodificables(){
        for(int i = 0 ; i < 9 ; i++)
        {
           for(int j = 0 ; j < 9 ; j++)
           {
               if(r[i][j] != 0){
                   Inmodificable inmod = new Inmodificable(i,j);
                   inmodificables.add(inmod);
               }
                   
           } 
        }
            
   }
   //RESTRICCIONES ---------------------------------------------
   public boolean esModificable(int f, int c){
       boolean posibleModificar = true;
       for(Inmodificable inmod: inmodificables){
           if( inmod.fila == f && inmod.columna == c)
               posibleModificar = false;
       }
       return posibleModificar;
   }
   public boolean unicoEnFilaColumna(int f, int c, int numero){
        boolean unico= true;
        int i = 0;
        while(i<9 && unico){
            if(r[f][i] == numero || r[i][c] == numero)
                unico = false;
            i++;
        }
        return unico;
   }
   public boolean unicoEnSuConjunto(int f, int c, int numero){
        int f_comienzo = 0;
        int c_comienzo = 0;
        boolean unico = true;
        //conjunto 1 weno
        if(f < 3 && c < 3){
            f_comienzo = 0;
            c_comienzo = 0;
        }
        //conjunto 2 weno
        if(c > 2 && c < 6 && f < 3){
            f_comienzo = 0;
            c_comienzo = 3;
        }
        //conjunto 3 weno
        if(c > 5 && f < 3){
            f_comienzo = 0;
            c_comienzo = 6;
        }
        //conjunto 4 weno
        if(f<6 && f>2 && c<3){
            f_comienzo = 3;
            c_comienzo = 0;
        }
        //conjunto 5 weno
        if(f<6 && f>2 && c<6 && c>2){
            f_comienzo = 3;
            c_comienzo = 3;
        }    
        //conjunto 6 weno
        if(f<6 && f>2 && c>5){
            f_comienzo = 3;
            c_comienzo = 6  ;
        }
        //conjunto 7 weno
        if(f>5 && c<3){
            f_comienzo = 6;
            c_comienzo = 0;
        
        }
        //conjunto 8
        if(f>5 && c<6 && c>2){
            f_comienzo = 6;
            c_comienzo = 3;
        }
        //conjunto 9
        if(f>5 && c>5){
            f_comienzo = 6;
            c_comienzo = 6;
        }
        for(int i = f_comienzo; i < f_comienzo+3; i++){
            for(int j = c_comienzo; j<c_comienzo+3; j++){
                if(r[i][j] == numero)
                    unico = false;
            }
        }
        return unico;
   }
   public void agregar(int f, int c, int numero)
   {
        r[f][c] = numero;
   }
    public void quitar(int f, int c)
   {
        r[f][c] = 0;
    }
    public void showTablero(){
        for(int i = 0 ; i < 9 ; i++)
        {
            System.out.print("[ ");
            for(int j = 0 ; j < 9 ; j++){
                System.out.print(r[i][j]+" ");
            }
            System.out.print("]");
            System.out.println();
        }
    }
    
}                                                                     //                              