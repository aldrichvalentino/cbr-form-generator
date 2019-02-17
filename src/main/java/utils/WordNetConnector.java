package utils;

import edu.smu.tspell.wordnet.WordNetDatabase;

public class WordNetConnector {
    private static WordNetConnector connector = null;
    private WordNetDatabase database = null;

    private WordNetConnector(String wordNetDir) {
        System.setProperty("wordnet.database.dir", wordNetDir);
        database = WordNetDatabase.getFileInstance();
    }

    public static WordNetConnector getInstance(String wordNetDir) {
        if (connector == null) {
            connector = new WordNetConnector(wordNetDir);
        }
        return connector;
    }

    public WordNetDatabase getDatabase() {
        return database;
    }
}