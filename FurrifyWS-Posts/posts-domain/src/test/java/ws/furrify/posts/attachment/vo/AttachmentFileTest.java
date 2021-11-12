package ws.furrify.posts.attachment.vo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ws.furrify.posts.attachment.AttachmentExtension;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AttachmentFileTest {

    private static String filename;
    private static AttachmentExtension extension;
    private static URI fileUri;
    private static String md5;

    @BeforeAll
    static void setUp() throws MalformedURLException, URISyntaxException {
        filename = "test.psd";
        extension = AttachmentExtension.PSD;
        fileUri = new URI("/test");
        md5 = "3c518eeb674c71b30297f072fde7eba5";
    }

    @Test
    @DisplayName("Create AttachmentFile")
    void of() {
        // Given filename, extension, fileUri and md5
        // When builder build
        var attachmentFile = AttachmentFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .md5(md5)
                .build();
        // Then return created AttachmentFile
        assertAll(
                () -> assertEquals(
                        filename,
                        attachmentFile.getFilename(),
                        "Created filename is not the same."
                ),
                () -> assertEquals(
                        extension,
                        attachmentFile.getExtension(),
                        "Created extension is not the same."
                ),
                () -> assertEquals(
                        fileUri,
                        attachmentFile.getFileUri(),
                        "Created fileUri is not the same."
                ),
                () -> assertEquals(
                        md5,
                        attachmentFile.getMd5(),
                        "Created md5 is not the same."
                )
        );
    }

    @Test
    @DisplayName("Create AttachmentFile from invalid filename")
    void of2() {
        // Given invalid filename
        String filename = "example";
        // When builder
        var attachmentFileBuilder = AttachmentFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                attachmentFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AttachmentFile from invalid extension")
    void of3() {
        // Given not matching declared extension
        AttachmentExtension extension = AttachmentExtension.BLEND;
        // When builder
        var attachmentFileBuilder = AttachmentFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                attachmentFileBuilder::build,
                "Exception was not thrown."
        );
    }

    @Test
    @DisplayName("Create AttachmentFile from invalid files hash")
    void of4() {
        // Given invalid md5
        String md5 = "test";
        // When builder
        var attachmentFileBuilder = AttachmentFile.builder()
                .filename(filename)
                .extension(extension)
                .fileUri(fileUri)
                .md5(md5);
        // Then throw IllegalStateException
        assertThrows(
                IllegalStateException.class,
                attachmentFileBuilder::build,
                "Exception was not thrown."
        );
    }
}