package de.torsten.horn;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/**
 * Dieses Programmierbeispiel demonstriert, wie in einer Swing-GUI-Anwendung mit dem
 * {@link <a href="http://docs.oracle.com/javase/8/docs/api/javax/swing/SwingWorker.html">SwingWorker</a>}
 * zeitaufwaendige Aufgaben in einen Background-Thread verlagert werden koennen,
 * damit das GUI bedienbar bleibt, und wie trotzdem Zwischenergebnisse im GUI angezeigt werden koennen.
 * </br>
 * Das Besondere an diesem Programmierbeispiel ist, dass die Swing-GUI-Klasse und die Worker-Klasse,
 * welche die zeitaufwaendige Aufgabe ausfuehrt, entkoppelt sind.
 * Normalerweise muss die Worker-Klasse Zwischenergebnisse ueber die SwingWorker.publish()-Methode uebergeben,
 * und ist damit fest mit dem Swing-GUI gekoppelt.
 * In diesem Beispiel ist die Worker-Klasse eigenstaendig und unabhaengig vom Swing-GUI.
 * Sie kann als Konsolenprogramm oder mit unterschiedlichen GUIs verwendet werden.
 * Trotzdem werden Zwischenergebnisse ueber einen Callback uebermittelt.
 */
public class SwingWorkerCallbackGui
{
   public static void main( String[] args )
   {
      starteSwingWorkerCallbackGui();
   }

   public static void starteSwingWorkerCallbackGui()
   {
      final String titel = "Textdateianzeige";

      final JTextField textdatei = new JTextField();
      textdatei.setText( (new File( "src/test/java/de/torsten/horn/SwingWorkerCallbackGui.java" )).getAbsolutePath() );

      final String[] charsetAnzeigeNamen = new String[] {
            StandardCharsets.ISO_8859_1.name(), StandardCharsets.UTF_8.name(), StandardCharsets.UTF_16.name() };
      final JComboBox<String> charEncod = new JComboBox<String>( charsetAnzeigeNamen );

      final JTextArea textArea = new JTextArea();
      textArea.setFont( new Font( "Monospaced", Font.PLAIN, 11 ) );
      textArea.setEditable( false );
      DefaultCaret caret = (DefaultCaret) textArea.getCaret();
      caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE );
      JScrollPane areaScrollPane = new JScrollPane( textArea );
      areaScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
      areaScrollPane.setPreferredSize( new Dimension( 250, 250 ) );

      final JButton button = new JButton( "Textdatei anzeigen" );
      button.addActionListener( new ActionListener() {
         @Override
         public void actionPerformed( ActionEvent ev ) {
            button.setEnabled( false );
            textArea.setBackground( Color.WHITE );
            textArea.setForeground( Color.BLACK );
            // Starte Extra-Thread per SwingWorker, damit der Event Dispatch Thread (EDT) nicht blockiert wird:
            (new TextDateiLesenSwingWorker( textdatei.getText(), charEncod.getSelectedItem().toString(), button, textArea )).execute();
         }
      } );

      JPanel panel1 = new JPanel();
      panel1.setBorder( BorderFactory.createEmptyBorder( 30, 30, 5, 30 ) );
      panel1.setLayout( new GridLayout( 1, 1 ) );
      panel1.add( new JLabel( titel ) );

      JPanel panel2a = new JPanel();
      panel2a.setBorder( BorderFactory.createEmptyBorder( 5, 30, 5, 0 ) );
      panel2a.setLayout( new GridLayout( 3, 1, 15, 15 ) );
      panel2a.add( new JLabel( "Textdatei" ) );
      panel2a.add( new JLabel( "Zeichenkodierung" ) );
      panel2a.add( new JLabel( "" ) );

      JPanel panel2b = new JPanel();
      panel2b.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 30 ) );
      panel2b.setLayout( new GridLayout( 3, 1, 15, 15 ) );
      panel2b.add( textdatei );
      panel2b.add( charEncod );
      panel2b.add( button );

      JPanel panel3 = new JPanel();
      panel3.setBorder( BorderFactory.createEmptyBorder( 5, 30, 30, 30 ) );
      panel3.setLayout( new GridLayout( 1, 1 ) );
      panel3.add( areaScrollPane );

      JFrame frame = new JFrame( titel );
      frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
      frame.setResizable( false );
      frame.setLocation( 100, 100 );
      frame.setLayout( new BorderLayout( 10, 10 ) );
      frame.add( panel1,  BorderLayout.PAGE_START );
      frame.add( panel2a, BorderLayout.WEST );
      frame.add( panel2b, BorderLayout.EAST );
      frame.add( panel3,  BorderLayout.PAGE_END );
      frame.pack();
      frame.setVisible( true );
   }

   /** Auf Swing-Komponenten soll nur vom Swing Event Dispatch Thread (EDT) aus zugegriffen werden,
       um Multithreading-Probleme zu vermeiden.
       Andererseits sollen zeitaufwaendige Aufgaben nicht im EDT ausgefuehrt werden,
       damit dieser nicht blockiert wird und das GUI bedienbar bleibt.
       Der "SwingWorker" loest die Aufgabe, indem die zeitaufwaendigen Aufgaben in einen Extra-Background-Thread verlagert werden,
       und ueber einen Kommunikationsmechanismus die Swing-Komponenten Thread-sicher asynchron im EDT manipuliert werden. */
   public static final class TextDateiLesenSwingWorker extends SwingWorker<Boolean,String>
   {
      String    textdatei;
      String    charEncod;
      JButton   button;
      JTextArea textArea;

      public TextDateiLesenSwingWorker( String textdatei, String charEncod, JButton button, JTextArea textArea )
      {
         this.textdatei = textdatei;
         this.charEncod = charEncod;
         this.button    = button;
         this.textArea  = textArea;
      }

      /** Die "doInBackground()"-Methode wird in einem eigenen Background-Thread ausgefuehrt.
          Sie darf nicht direkt Swing-Komponenten manipulieren. */
      @Override
      protected Boolean doInBackground() throws Exception
      {
         final class SwingMeldungenCallback implements DateiLeserMitCallback.MeldungenCallback<String>
         {
            /** Die "publish()"-Methode sendet Zwischenergebnis-Objekte an die "process()"-Methode,
                in welcher Swing-Aktionen Thread-sicher asynchron im EDT ausgefuehrt werden. */
            @SuppressWarnings("synthetic-access")
            @Override public void ausgabeMeldung( String s ) { publish( s ); }
         }

         /** Aufruf des eigentlichen Jobs. Zwischenergebnisse werden per Callback returniert.
             Das finale Return-Ergebnis kann in der "done()"-Methode per "get()" abgefragt werden. */ 
         return DateiLeserMitCallback.liesTextdatei( textdatei, charEncod, new SwingMeldungenCallback() );
      }

      /** Die "process()"-Methode empfaengt die ueber "publish()" uebergebenen Objekte.
          Sie laeuft im EDT und kann asynchron Swing-Komponenten manipulieren. */
      @Override
      protected void process( List<String> chunks )
      {
         if( chunks != null && textArea != null ) {
            for( String s : chunks ) {
               textArea.append( s + "\n" );
            }
         }
      }

      /** Die "done()"-Methode wird nach Beendigung der "doInBackground()"-Methode aufgerufen.
          Sie laeuft im EDT und kann Swing-Komponenten manipulieren. */
      @Override
      protected void done()
      {
         Boolean ret = Boolean.FALSE;
         try {
            /** Abfrage der Ergebnisses der "doInBackground()"-Methode: */
            ret = get();
         } catch( ExecutionException | InterruptedException | CancellationException ex ) { /* ok */ }
         if( textArea != null ) {
            if( ret != null && ret.booleanValue() ) {
               textArea.setForeground( new Color( 0x008800 ) );
            } else {
               textArea.setBackground( new Color( 0xFFFFEE ) );
               textArea.setForeground( Color.RED );
            }
         }
         if( button != null ) { button.setEnabled( true ); }
      }
   }
}