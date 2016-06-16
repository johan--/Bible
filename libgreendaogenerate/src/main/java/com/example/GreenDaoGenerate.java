package com.example;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerate {

    public static final String SQL_DB = "com.papa.bible.data.db.database";
    public static final String SQL_DAO = "com.papa.bible.data.db.dao";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, SQL_DB);
        schema.setDefaultJavaPackageDao(SQL_DAO);
        schema.enableKeepSectionsByDefault();
        createBookTable(schema);

        new DaoGenerator().generateAll(schema, getPath());
    }

    private static void createBookTable(Schema schema) {
        Entity bookEntity = schema.addEntity("BookEntity");
        bookEntity.addIdProperty().primaryKey();
        bookEntity.addStringProperty("path").notNull().unique();
        bookEntity.addStringProperty("baseUrl");
        bookEntity.addStringProperty("name");
        bookEntity.setHasKeepSections(true);
        bookEntity.implementsSerializable();

        Entity bookChapterEntity = schema.addEntity("BookChapterEntity");
        bookChapterEntity.addIdProperty().primaryKey();
        bookChapterEntity.addStringProperty("title");
        bookChapterEntity.addStringProperty("resourceId");
        bookChapterEntity.addStringProperty("href");
        bookChapterEntity.implementsSerializable();

        Property chapterBookIdEntity = bookChapterEntity.addLongProperty("bookId").getProperty();
        bookEntity.addToMany(bookChapterEntity, chapterBookIdEntity);
        bookChapterEntity.addToOne(bookEntity, chapterBookIdEntity);

        Entity bookContentEntity = schema.addEntity("BookContentEntity");
        bookContentEntity.addIdProperty().primaryKey();
        bookContentEntity.addStringProperty("resourceId");
        bookContentEntity.addStringProperty("path");
        bookContentEntity.addStringProperty("urlResource");
        bookContentEntity.implementsSerializable();

        Property contentBookIdEntity = bookContentEntity.addLongProperty("bookId").getProperty();
        bookEntity.addToMany(bookContentEntity, contentBookIdEntity);
        bookContentEntity.addToOne(bookEntity, contentBookIdEntity);

        Entity bookmarkEntity = schema.addEntity("BookmarkEntity");
        bookmarkEntity.addIdProperty().primaryKey();
        bookmarkEntity.addStringProperty("resourceId").unique();
        bookmarkEntity.addStringProperty("urlResource");
        bookmarkEntity.addDateProperty("date");
        bookmarkEntity.addIntProperty("scrollX");
        bookmarkEntity.addIntProperty("scrollY");
        bookmarkEntity.implementsSerializable();

        Property markBookIdEntity = bookmarkEntity.addLongProperty("bookId").getProperty();
        bookEntity.addToMany(bookmarkEntity, markBookIdEntity);
        bookmarkEntity.addToOne(bookEntity, markBookIdEntity);

        Entity audioEntity = schema.addEntity("AudioEntity");
        audioEntity.addIdProperty().primaryKey();
        audioEntity.addStringProperty("audioPath").unique();
        audioEntity.addStringProperty("audioName");
        audioEntity.implementsSerializable();

        Property audioBookIdEntity = audioEntity.addLongProperty("bookId").getProperty();
        bookEntity.addToMany(audioEntity, audioBookIdEntity);
        audioEntity.addToOne(bookEntity, audioBookIdEntity);

    }


    /**
     * 获取程序的根目录
     *
     * @return
     */
    private static String getPath() {

        String path = new StringBuilder()
                .append("app")
                .append(File.separator)
                .append("src")
                .append(File.separator)
                .append("main")
                .append(File.separator)
                .append("java")
                .append(File.separator).toString();
        return new File(path).getAbsolutePath();
    }
}
