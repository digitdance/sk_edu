package org.springcrazy.modules.front.controller;

import com.tencent.cloud.CosStsClient;
import com.tencent.cloud.Response;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeMap;

@RestController
@RequestMapping("/common/cos")
public class CosCredentialController {
    @Value("$" + "{oss.secret-id}")
    private String secretId;

    @Value("$" + "{oss.secret-key}")
    private String secretKey;

    @Value("$" + "{oss.bucket}")
    private String bucket;

    @Value("$" + "{oss.region}")
    private String region;

    @GetMapping("/credentials")
    public R credentials() {
        if (SecureUtil.getUserId() == null) {
            return R.fail("Please sign in before uploading");
        }

        TreeMap<String, Object> config = new TreeMap<>();
        config.put("secretId", secretId);
        config.put("secretKey", secretKey);
        config.put("durationSeconds", 1800);
        config.put("bucket", bucket);
        config.put("region", region);
        config.put("allowPrefixes", new String[]{"lecturer-files/*"});
        config.put("allowActions", new String[]{
                "cos:PutObject", "cos:PostObject", "cos:InitiateMultipartUpload",
                "cos:ListMultipartUploads", "cos:ListParts", "cos:UploadPart",
                "cos:CompleteMultipartUpload", "cos:AbortMultipartUpload"
        });

        try {
            Response response = CosStsClient.getCredential(config);
            return R.data(response);
        } catch (Exception e) {
            return R.fail("Unable to create temporary upload credentials");
        }
    }
}