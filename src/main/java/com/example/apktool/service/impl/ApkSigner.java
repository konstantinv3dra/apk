package com.example.apktool.service.impl;

import com.example.apktool.entity.SignatureConfig;
import com.example.apktool.utils.ShellExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApkSigner {
    
    private final ShellExecutor shellExecutor;
    
    /**
     * Sign an APK file using the specified signature configuration
     *
     * @param apkPath Path to the APK file to sign
     * @param signatureConfig Signature configuration to use
     * @return Path to the signed APK file
     */
    public String signApk(String apkPath, SignatureConfig signatureConfig) throws IOException, InterruptedException {
        log.info("Signing APK: {} with signature: {}", apkPath, signatureConfig.getName());
        
        String alignedApkPath = zipalignApk(apkPath);
        
        String outputPath = alignedApkPath.replace(".apk", "-signed.apk");
        
        String signCommand = String.format(
            "jarsigner -verbose -keystore %s -storepass %s -keypass %s -signedjar %s %s %s",
            signatureConfig.getKeystorePath(),
            signatureConfig.getPassword(),
            signatureConfig.getKeyPassword(),
            outputPath,
            alignedApkPath,
            signatureConfig.getAlias()
        );
        
        List<String> output = shellExecutor.execute(signCommand);
        
        verifySignature(outputPath);
        
        return outputPath;
    }
    
    /**
     * Align an APK using zipalign
     */
    private String zipalignApk(String apkPath) throws IOException, InterruptedException {
        String outputPath = apkPath.replace(".apk", "-aligned.apk");
        String alignCommand = String.format("zipalign -v 4 %s %s", apkPath, outputPath);
        shellExecutor.execute(alignCommand);
        return outputPath;
    }
    
    /**
     * Verify the signature of an APK
     */
    private void verifySignature(String apkPath) throws IOException, InterruptedException {
        String verifyCommand = String.format("jarsigner -verify -verbose %s", apkPath);
        shellExecutor.execute(verifyCommand);
    }
}
