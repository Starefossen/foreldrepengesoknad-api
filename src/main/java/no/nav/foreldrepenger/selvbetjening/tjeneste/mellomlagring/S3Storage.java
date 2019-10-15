package no.nav.foreldrepenger.selvbetjening.tjeneste.mellomlagring;

import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;

public class S3Storage implements Storage {

    private static final Logger LOG = LoggerFactory.getLogger(S3Storage.class);

    private static final String BUCKET_FORELDREPENGER = "foreldrepengesoknad";
    private static final String BUCKET_FORELDREPENGER_MELLOMLAGRING = "mellomlagring";

    private final AmazonS3 s3;

    public S3Storage(AmazonS3 s3) {
        this.s3 = s3;
        try {
            ensureBucketExists(BUCKET_FORELDREPENGER, 365);
            ensureBucketExists(BUCKET_FORELDREPENGER_MELLOMLAGRING, 1);
        } catch (Exception e) {
            LOG.error("Kunne ikke sette opp bøtter", e);
        }
    }

    @Override
    public void put(String directory, String key, String value) {
        writeString(BUCKET_FORELDREPENGER, directory, key, value);
    }

    @Override
    public void putTmp(String directory, String key, String value) {
        writeString(BUCKET_FORELDREPENGER_MELLOMLAGRING, directory, key, value);
    }

    @Override
    public Optional<String> get(String directory, String key) {
        return Optional.ofNullable(readString(BUCKET_FORELDREPENGER, directory, key));
    }

    @Override
    public Optional<String> getTmp(String directory, String key) {
        return Optional.ofNullable(readString(BUCKET_FORELDREPENGER_MELLOMLAGRING, directory, key));
    }

    @Override
    public void delete(String directory, String key) {
        deleteString(BUCKET_FORELDREPENGER, directory, key);
    }

    @Override
    public void deleteTmp(String directory, String key) {
        deleteString(BUCKET_FORELDREPENGER_MELLOMLAGRING, directory, key);
    }

    private void ensureBucketExists(String bucketName) {
        LOG.info("Sjekker om bøtte {} eksisterer", bucketName);
        boolean bucketExists = s3.listBuckets().stream()
                .anyMatch(b -> b.getName().equals(bucketName));
        if (!bucketExists) {
            createBucket(bucketName);
        } else {
            LOG.info("Bøtte {} eksisterer", bucketName);
        }
    }

    private void ensureBucketExists(String bucketName, Integer expirationInDays) {
        ensureBucketExists(bucketName);
        setLifeCycleConfig(bucketName, objectExpiresInDays(expirationInDays));
    }

    private void setLifeCycleConfig(String bucketName, BucketLifecycleConfiguration expiry) {
        try {
            LOG.info("Setter lifecycle config for bøtta {}", bucketName);
            s3.setBucketLifecycleConfiguration(bucketName, expiry);
        } catch (Exception e) {
            LOG.info("Kunne ikke setter lifecycle config for bøtta {}", bucketName, e);
        }
    }

    private void createBucket(String bucketName) {
        LOG.info("Lager bøtte {}", bucketName);
        s3.createBucket(new CreateBucketRequest(bucketName)
                .withCannedAcl(CannedAccessControlList.Private));
    }

    private void writeString(String bucketName, String directory, String key, String value) {
        s3.putObject(bucketName, fileName(directory, key), value);
    }

    private String readString(String bucketName, String directory, String key) {
        String path = fileName(directory, key);
        try {
            S3Object object = s3.getObject(bucketName, path);
            return new BufferedReader(new InputStreamReader(object.getObjectContent()))
                    .lines()
                    .collect(joining("\n"));
        } catch (AmazonS3Exception e) {
            LOG.trace("Kunne ikke hente {}, finnes sannsynligvis ikke", path, e);
            return null;
        }

    }

    private static BucketLifecycleConfiguration objectExpiresInDays(Integer days) {
        return new BucketLifecycleConfiguration().withRules(
                new BucketLifecycleConfiguration.Rule()
                        .withId("soknad-retention-policy-" + days)
                        .withFilter(new LifecycleFilter())
                        .withStatus(BucketLifecycleConfiguration.ENABLED)
                        .withExpirationInDays(days));
    }

    private void deleteString(String bucketName, String directory, String key) {
        s3.deleteObject(bucketName, fileName(directory, key));
    }

    private static String fileName(String directory, String key) {
        return directory + "_" + key;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [s3=" + s3 + "]";
    }

    @Override
    public String ping() {
        ensureBucketExists(BUCKET_FORELDREPENGER_MELLOMLAGRING);
        return "OK";
    }

    @Override
    public URI pingURI() {
        return URI.create(s3.getUrl(BUCKET_FORELDREPENGER_MELLOMLAGRING, "42").toString());
    }
}
