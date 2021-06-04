package org.kimios.zipper.controller.impl;

import org.kimios.converter.source.InputSourceFactory;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.controller.IDmEntityController;
import org.kimios.kernel.controller.IDocumentController;
import org.kimios.kernel.controller.IDocumentVersionController;
import org.kimios.kernel.controller.IFolderController;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.dms.model.Folder;
import org.kimios.kernel.security.model.Session;
import org.kimios.zipper.controller.IZipperController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipperController implements IZipperController {

    private IDmEntityController dmEntityController;
    private IDocumentVersionController documentVersionController;
    private IDocumentController documentController;
    private IFolderController folderController;

    @Override
    public File makeZipWithEntities(Session session, List<Long> dmEntityUidList) throws ConfigException, IOException {

        String zipFileName = session.getUserName() + "@" + session.getUserSource() + "_" + new Date().toInstant().toEpochMilli();
        LinkedHashMap<String, InputStream> inputStreamLinkedHashMap = new LinkedHashMap<>();
        List<DMEntityImpl> dmEntityList = new ArrayList<>();
        for (Long uid: dmEntityUidList) {
            dmEntityList.add(this.dmEntityController.getEntity(session, uid));
        }
        this.prepareZipFileInputStreams(session, dmEntityList, inputStreamLinkedHashMap, "");
        File zip = makeZipFromLinkedHashMap(inputStreamLinkedHashMap, zipFileName);
        return zip;
    }

    private static File makeZipFromLinkedHashMap(
            LinkedHashMap<String, InputStream> inputStreamLinkedHashMap,
            String zipFileName
    ) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String entityName : inputStreamLinkedHashMap.keySet()) {
            InputStream inputStream = inputStreamLinkedHashMap.get(entityName);

            String entryName = inputStream == null ?
                    entityName + "/" :
                    entityName;
            ZipEntry zipEntry = new ZipEntry(entryName);
            zipOut.putNextEntry(zipEntry);

            if (inputStream != null) {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = inputStream.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                inputStream.close();
            }
            zipOut.closeEntry();
        }
        zipOut.close();

        return new File(zipFileName);
    }

    private void prepareZipFileInputStreams(
            Session session,
            List<? extends DMEntityImpl> dmEntityList,
            LinkedHashMap<String, InputStream> inputStreamLinkedHashMap,
            String path
    ) throws IOException {
        for (DMEntityImpl dmEntity : dmEntityList) {
            if (dmEntity instanceof Document) {
                inputStreamLinkedHashMap.put(
                        (path.equals("") ? path : path + "/")
                                + dmEntity.getName()
                                +
                                ((((Document) dmEntity).getExtension() != null
                                && !((Document) dmEntity).getExtension().isEmpty()) ?
                                "." + ((Document) dmEntity).getExtension() :
                                ""),
                        InputSourceFactory.getInputSource(
                                documentVersionController.getLastDocumentVersion(session, dmEntity.getUid()),
                                UUID.randomUUID().toString()
                        ).getInputStream()
                );
            } else {
                if (dmEntity instanceof Folder) {
                    inputStreamLinkedHashMap.put(path + "/" + dmEntity.getName() + "/", null);
                    List<DMEntityImpl> entities =  Stream.concat(
                            this.documentController.getDocuments(session, dmEntity.getUid())
                                    .stream().map(document -> (DMEntityImpl)document),
                            this.folderController.getFolders(session, dmEntity.getUid())
                                    .stream().map(folder -> (DMEntityImpl)folder)
                    ).collect(Collectors.toList());
                    this.prepareZipFileInputStreams(
                            session,
                            entities,
                            inputStreamLinkedHashMap,
                            (path.equals("") ? path : path + "/") + dmEntity.getName()
                    );
                }
            }
        }
    }

    public IDmEntityController getDmEntityController() {
        return dmEntityController;
    }

    public void setDmEntityController(IDmEntityController dmEntityController) {
        this.dmEntityController = dmEntityController;
    }

    public IDocumentVersionController getDocumentVersionController() {
        return documentVersionController;
    }

    public void setDocumentVersionController(IDocumentVersionController documentVersionController) {
        this.documentVersionController = documentVersionController;
    }

    public IDocumentController getDocumentController() {
        return documentController;
    }

    public void setDocumentController(IDocumentController documentController) {
        this.documentController = documentController;
    }

    public IFolderController getFolderController() {
        return folderController;
    }

    public void setFolderController(IFolderController folderController) {
        this.folderController = folderController;
    }

    /*    private static void addEntityToZip(DMEntityImpl dmEntity, String entityName, ZipOutputStream zipOut) throws IOException {
        if (dmEntity instanceof Folder) {
            zipOut.putNextEntry(new ZipEntry(entityName + "/"));
            zipOut.closeEntry();
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }*/
}
