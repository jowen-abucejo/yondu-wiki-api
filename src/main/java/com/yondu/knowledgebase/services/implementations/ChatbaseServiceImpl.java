package com.yondu.knowledgebase.services.implementations;

import com.yondu.knowledgebase.DTO.directory.DirectoryDTOMapper;
import com.yondu.knowledgebase.entities.Directory;
import com.yondu.knowledgebase.entities.PageVersion;
import com.yondu.knowledgebase.entities.Post;
import com.yondu.knowledgebase.services.ChatbaseService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.util.stream.Collectors;

@Service
public class ChatbaseServiceImpl implements ChatbaseService {

    private Logger log = LoggerFactory.getLogger(ChatbaseServiceImpl.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    private static final String CHATBASE_UPDATE_ENDPOINT = "https://www.chatbase.co/api/v1/update-chatbot-data";
    private static final String CHATBASE_SOURCE_TEXT = "cb_source_text.txt";

    @Value("${cb.token}")
    private String CHATBASE_TOKEN;

    /**
     * Change this in application.properties
     */
    @Value("${cb.chatbot-id}")
    private String CHATBOT_ID;
    @Value("${cb.chatbot-name}")
    private String CHATBOT_NAME;

    @Override
    public void updateChatbot(PageVersion pageVersion) {
        log.info("ChatbaseServiceImpl.updateChatbot()");

        try{
            String newContent = formatNewPage(pageVersion);
            String updatedContent = writeChatbaseSourceText(newContent);

            if(updatedContent.length() > 500){
                JSONObject requestBody = new JSONObject();
                requestBody.put("chatbotId", CHATBOT_ID);
                requestBody.put("chatbotName", CHATBOT_NAME);
                requestBody.put("sourceText", updatedContent);

                WebClient.ResponseSpec responseSpec = webClientBuilder.build()
                        .post()
                        .uri(CHATBASE_UPDATE_ENDPOINT)
                        .header("Authorization", "Bearer " + CHATBASE_TOKEN)
                        .header("Content-Type", "application/json")
                        .body(Mono.just(requestBody.toString()), String.class)
                        .retrieve();

                Mono<String> responseBodyMono = responseSpec.bodyToMono(String.class);
                responseBodyMono.subscribe(response -> {
                    log.info("API RESPONSE: " + response);
                });
            }

        }catch(Exception ex){
            log.info("ex : " + ex.getMessage());
        }
    }

    @Override
    public void updateChatbot(Post post) {
        log.info("ChatbaseServiceImpl.updateChatbot()");

        try{
            String newContent = formatNewPage(post);
            String updatedContent = writeChatbaseSourceText(newContent);

            JSONObject requestBody = new JSONObject();
            requestBody.put("chatbotId", CHATBOT_ID);
            requestBody.put("chatbotName", CHATBOT_NAME);
            requestBody.put("sourceText", updatedContent);

            WebClient.ResponseSpec responseSpec = webClientBuilder.build()
                    .post()
                    .uri(CHATBASE_UPDATE_ENDPOINT)
                    .header("Authorization", "Bearer " + CHATBASE_TOKEN)
                    .header("Content-Type", "application/json")
                    .body(Mono.just(requestBody.toString()), String.class)
                    .retrieve();

            Mono<String> responseBodyMono = responseSpec.bodyToMono(String.class);
            responseBodyMono.subscribe(response -> {
                log.info("API RESPONSE: " + response);
            });

        }catch(Exception ex){
            log.info("ex : " + ex.getMessage());
        }
    }

    private String formatNewPage(PageVersion pageVersion) {
        log.info("ChatbaseServiceImpl.formatNewPage()");

        JSONObject newContent = new JSONObject();
        newContent.put("post_id", pageVersion.getPage().getId());
        newContent.put("post_author", pageVersion.getPage().getAuthor().getFirstName() + " " + pageVersion.getPage().getAuthor().getLastName());
        newContent.put("post_type", pageVersion.getPage().getType().toLowerCase());
        newContent.put("post_title", pageVersion.getTitle());
        newContent.put("post_content", pageVersion.getContent());

        Directory pageDirectory = pageVersion.getPage().getDirectory();
        String fullPath = DirectoryDTOMapper.getPathFromParentToChild(pageDirectory).stream().map(directory -> directory.name() + "/").collect(Collectors.joining());

        newContent.put("post_directory", fullPath);


        return newContent.toString();
    }

    private String formatNewPage(Post post) {
        log.info("ChatbaseServiceImpl.formatNewPage()");

        JSONObject newContent = new JSONObject();
        newContent.put("post_id", post.getId());
        newContent.put("post_author", post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName());
        newContent.put("post_type", "discussion");
        newContent.put("post_title", post.getTitle());
        newContent.put("post_content", post.getContent().replaceAll("<[^>]+>", ""));

        return newContent.toString();
    }

    private boolean isChatbaseFileAlreadyExist() {
        File file = new File(CHATBASE_SOURCE_TEXT);
        return file.exists();
    }

    private String readChatbaseSourceText() {
        log.info("ChatbaseServiceImpl.readChatbaseSourceText()");
        try{

            if(!isChatbaseFileAlreadyExist()){
                File file = new File(CHATBASE_SOURCE_TEXT);
                file.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(CHATBASE_SOURCE_TEXT));

            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();

            String content = contentBuilder.toString();

            return content;
        }catch(IOException error) {
            log.error(error.getMessage());
            return null;
        }
    }

    private String writeChatbaseSourceText(String newContent) {
        log.info("ChatbaseServiceImpl.writeChatbaseSourceText()");
        log.info("newContent : " + newContent);
        try{
            String currentContent = readChatbaseSourceText();

            if(currentContent != null){
                BufferedWriter writer = new BufferedWriter(new FileWriter(CHATBASE_SOURCE_TEXT));

                StringBuilder contentBuilder = new StringBuilder();
                contentBuilder.append(currentContent).append("\n");
                contentBuilder.append(newContent).append("\n");

                writer.write(contentBuilder.toString());

                writer.close();

                return contentBuilder.toString();
            }else{
                return null;
            }
        }catch (IOException error){
            log.error(error.getMessage());
            return null;
        }
    }
}
