package com.papa.bible.data;

import android.content.Context;
import android.content.Intent;


import com.papa.bible.ChapterActivity;
import com.papa.bible.bean.BookDecompressed;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.util.StringUtil;

/**
 * Created by rodrigo.almeida on 28/04/15.
 */
public class FolioReader  {

    private Context context;
    private FileInputStream fileInputStream;
    private Book book;
    private BookDecompressed bookDecompressed;

    public FolioReader(Context context) {
        this.context = context;
        bookDecompressed = new BookDecompressed();
    }


    public void openBook(String ePubpath) {
        try {
            this.fileInputStream = new FileInputStream(ePubpath);

            String folderDec = FolioReaderUtils.getPathePubDec(context) + FolioReaderUtils.getFilename(ePubpath) + "/";

            if (!FolioReaderUtils.isDecompressed(context, ePubpath))
                FolioReaderUtils.unzipEPub(ePubpath, folderDec);

            String opfPath = FolioReaderUtils.getPathOPF(folderDec);

            this.book = new EpubReader().readEpub(fileInputStream);

            List<SpineReference> spineReferenceList = book.getSpine().getSpineReferences();

            bookDecompressed.setBook(book);

            if (spineReferenceList.size() > 0) {
                String path = StringUtil.substringBeforeLast(folderDec + opfPath + "/" +
                        spineReferenceList.get(0).getResource().getHref(), '/');
                bookDecompressed.setBaseURL("file://" + path + "/");
            } else bookDecompressed.setBaseURL(null);

            for (SpineReference s : spineReferenceList) {
                bookDecompressed.setUrlResources(folderDec + opfPath + "/" + s.getResource().getHref());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Configuration.setData(Configuration.KEY_BOOK, bookDecompressed);
        Intent intent = new Intent(context, ChapterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
