package com.papa.bible.data;

import android.content.Context;

import com.papa.bible.App;
import com.papa.bible.bean.BookDecompressed;
import com.papa.bible.data.db.database.AudioEntity;
import com.papa.bible.data.db.database.BookChapterEntity;
import com.papa.bible.data.db.database.BookContentEntity;
import com.papa.bible.data.db.database.BookEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.util.StringUtil;


public class FolioReader {

    private Context context;
    private FileInputStream fileInputStream;
    private Book book;
    private BookDecompressed bookDecompressed;
    private List<TOCReference> indexes;


    public FolioReader(Context context) {
        this.context = context;
        bookDecompressed = new BookDecompressed();
    }

    public long openBook(String ePubPath) {
        BookEntity entity = new BookEntity();
        try {
            entity.setPath(ePubPath);
            File file = new File(ePubPath);
            bookDecompressed.setEpubFileDir(file.getParent());
            this.fileInputStream = new FileInputStream(ePubPath);

            String folderDec = FolioReaderUtils.getPathePubDec(context) + FolioReaderUtils
                    .getFilename(ePubPath) + "/";

            if (!FolioReaderUtils.isDecompressed(context, ePubPath))
                FolioReaderUtils.unzipEPub(ePubPath, folderDec);

            String opfPath = FolioReaderUtils.getPathOPF(folderDec);

            this.book = new EpubReader().readEpub(fileInputStream);
            entity.setName(book.getTitle());
            List<SpineReference> spineReferenceList = book.getSpine().getSpineReferences();

            bookDecompressed.setBook(book);

            if (spineReferenceList.size() > 0) {
                String path = StringUtil.substringBeforeLast(folderDec + opfPath + "/" +
                        spineReferenceList.get(0).getResource().getHref(), '/');
                bookDecompressed.setBaseURL("file://" + path + "/");
                entity.setBaseUrl("file://" + path + "/");
            } else bookDecompressed.setBaseURL(null);

            for (SpineReference s : spineReferenceList) {
                bookDecompressed.setUrlResources(folderDec + opfPath + "/" + s.getResource()
                        .getHref());
            }
            long id = DataBaseManager.getInstance(App.getInstance()).getDaoSession().insert(entity);
            DataBaseManager.getInstance(App.getInstance()).getDaoSession()
                    .getBookChapterEntityDao().insertOrReplaceInTx(createChapterList(entity));
            DataBaseManager.getInstance(App.getInstance()).getDaoSession()
                    .getAudioEntityDao().insertOrReplaceInTx(createAudioList(entity));
            DataBaseManager.getInstance(App.getInstance()).getDaoSession()
                    .getBookContentEntityDao().insertOrReplaceInTx(createContentList(entity,
                    folderDec, opfPath));
            return id;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }


    private List<BookChapterEntity> createChapterList(BookEntity bookEntity) {
        List<TOCReference> indexes = new ArrayList<>();
        for (TOCReference tocReference : book.getTableOfContents().getTocReferences()) {
            getIndexRecursive(indexes, tocReference);
        }


        List<BookChapterEntity> bookChapterEntities = new ArrayList<>();
        for (TOCReference item : indexes) {
            BookChapterEntity entity = new BookChapterEntity();
            entity.setBookEntity(bookEntity);
            entity.setTitle(item.getTitle());
            entity.setResourceId(item.getResourceId());
            entity.setHref(item.getCompleteHref());
            bookChapterEntities.add(entity);
        }
        return bookChapterEntities;
    }

    private void getIndexRecursive(List<TOCReference> indexes, TOCReference tocReference) {
        if (tocReference != null)
            indexes.add(tocReference);

        for (TOCReference item : tocReference.getChildren()) {
            getIndexRecursive(indexes, item);
        }
    }


    private List<BookContentEntity> createContentList(BookEntity bookEntity, String folderDec,
                                                      String opfPath) {
        List<BookContentEntity> bookContentEntities = new ArrayList<>();
        List<SpineReference> spineReferenceList = book.getSpine().getSpineReferences();
        int size = spineReferenceList.size();
        for (int index = 0; index < size; index++) {
            BookContentEntity entity = new BookContentEntity();
            entity.setUrlResource(folderDec + opfPath + "/" + spineReferenceList.get(index)
                    .getResource()
                    .getHref());
            entity.setResourceId(spineReferenceList.get(index).getResourceId());
            entity.setBookEntity(bookEntity);

            bookContentEntities.add(entity);
        }
        return bookContentEntities;
    }

    private List<AudioEntity> createAudioList(BookEntity bookEntity){
        List<AudioEntity> audioEntities = new ArrayList<>();
        List<File> audioList = audioList(new File(bookDecompressed.getEpubFileDir()));
        for (File item : audioList) {
            AudioEntity entity = new AudioEntity();
            entity.setBookEntity(bookEntity);
            entity.setAudioName(getFileName(item));
            entity.setAudioPath(item.getPath());
            audioEntities.add(entity);
        }
        return audioEntities;
    }


    private String getFileName(File file) {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            name = name.substring(0, pos);
        }
        return name;
    }

    private List<File> audioList(File dir) {
        List<File> res = new ArrayList<File>();
        if (dir.isDirectory()) {
            File[] f = dir.listFiles();
            if (f != null) {
                for (int i = 0; i < f.length; i++) {
                    if (f[i].isDirectory()) {
                        res.addAll(audioList(f[i]));
                    } else {
                        String lowerCasedName = f[i].getName().toLowerCase();
                        if (lowerCasedName.endsWith(".ogg")) {
                            res.add(f[i]);
                        }
                    }
                }
            }
        }
        return res;
    }
}
