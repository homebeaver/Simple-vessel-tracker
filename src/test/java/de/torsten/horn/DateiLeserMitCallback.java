package de.torsten.horn;

import java.io.*;

public class DateiLeserMitCallback
{
   /** Ausfuehrung als Konsolenprogramm (ohne Swing-GUI). */
   public static void main( String[] args )
   {
      String textdatei = ( args != null && args.length > 0 ) ? args[0] : "src/test/java/de/torsten/horn/DateiLeserMitCallback.java";
      String charEncod = ( args != null && args.length > 1 ) ? args[1] : "ISO-8859-1";
      liesTextdatei( textdatei, charEncod, new KonsoleMeldungenCallback() );
   }

   /** Universelles Interface fuer Callback-Klasse zur Entkopplung der Meldung von Zwischenergebnissen
       waehrend der Verarbeitung, damit beliebige GUIs moeglich sind. */
   public static interface MeldungenCallback<V>
   {
      void ausgabeMeldung( V v );
   }

   /** Konkrete Implementierung der Callback-Klasse zur Ausgabe von Zwischenergebnissen auf der Konsole. */
   public static class KonsoleMeldungenCallback implements MeldungenCallback<String>
   {
      @Override public void ausgabeMeldung( String s ) { System.out.println( s ); }
   }

   /** "Arbeitsmethode" mit Callback zum Returnieren von Zwischenergebnissen waehrend der Verarbeitung. */
   public static Boolean liesTextdatei( String textdatei, String charEncod, MeldungenCallback<String> meldungenCallback )
   {
      String ausgabeAbgrenzung = "------------------------------------------------------------------";
      meldungenCallback.ausgabeMeldung( "\n" + ausgabeAbgrenzung );

      if( textdatei == null || textdatei.trim().length() == 0 ) {
         meldungenCallback.ausgabeMeldung( "Fehler: Dateiname ist leer." );
         return Boolean.FALSE;
      }
      if( !(new File( textdatei )).exists() ) {
         meldungenCallback.ausgabeMeldung( "Fehler: Datei " + textdatei + " existiert nicht." );
         return Boolean.FALSE;
      }

      meldungenCallback.ausgabeMeldung( "Textdateianzeige" );
      meldungenCallback.ausgabeMeldung( "Textdatei:        " + textdatei );
      meldungenCallback.ausgabeMeldung( "Zeichenkodierung: " + charEncod );
      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );

      try( BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( textdatei ), charEncod ) ) ) {
         String line;
         while( (line = in.readLine()) != null ) {
            meldungenCallback.ausgabeMeldung( line );
            // Zur Simulation berechnungsintensiver Aufgaben:
            Thread.sleep( 10 );
         }
      } catch( Exception ex ) {
         meldungenCallback.ausgabeMeldung( "Fehler-Exception: " + ex.getMessage() + "\n" + ex.toString() );
         return Boolean.FALSE;
      }

      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );
      meldungenCallback.ausgabeMeldung( "Fertig." );
      meldungenCallback.ausgabeMeldung( ausgabeAbgrenzung );
      return Boolean.TRUE;
   }
}
