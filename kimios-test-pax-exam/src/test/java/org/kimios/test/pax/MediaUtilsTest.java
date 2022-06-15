package org.kimios.test.pax;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MediaUtilsTest extends KimiosTest {

    @Test
    public void testMediaUtils() {
        String documentSampleResourceDir = "documents/";
        String documentSampleTmpDir = "/tmp/" + new Date().getTime() + "/";
        File file = new File(documentSampleTmpDir);
        file.mkdir();
        String[] resources = { "sample1.pdf", "sample2.png", "sample1", "sample2" };
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put(resources[0], "application/pdf");
        typeMap.put(resources[1], "image/png");
        typeMap.put(resources[2], "application/pdf");
        typeMap.put(resources[3], "image/png");

        List<MediaUtilsTestResource> mediaUtilsTestResourceList = Arrays.asList(resources).stream().map(resourceName ->
                new MediaUtilsTestResource(
                        documentSampleResourceDir + resourceName,
                        documentSampleTmpDir + resourceName,
                        typeMap.get(resourceName),
                        resourceName
                )
        ).collect(Collectors.toList());

        mediaUtilsTestResourceList.forEach(mediaUtilsTestResource ->
                {
                    try {
                        resourceToFile(mediaUtilsTestResource.getResourcePath(), mediaUtilsTestResource.getFileTmpPath());
                    } catch (IOException e) {
                        Assert.fail(e.getClass().getName() + " " + e.getMessage());
                    }
                }
        );

        mediaUtilsTestResourceList.forEach(mediaUtilsTestResource -> {
            String result = null;
            try {
                result = mediaUtilsController.detectMimeType(mediaUtilsTestResource.getFileTmpPath(), mediaUtilsTestResource.getResourceName());
            } catch (IOException e) {
                Assert.fail(e.getClass().getName() + " " + e.getMessage());
            }
            Assert.assertEquals(mediaUtilsTestResource.getMediaType(), result);
        });


    }
}
