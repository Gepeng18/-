//package site.pyyf.community.controller;
//
//
//import com.alibaba.dubbo.config.annotation.Reference;
//import site.pyyf.community.annotation.LoginRequired;
//import site.pyyf.commons.utils.MultipartSerializable;
//import site.pyyf.community.entity.File;
//import site.pyyf.commons.entity.Page;
//
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//import site.pyyf.community.service.fileSystemService;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.*;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Controller
//public class FastdfsController {
//
//    private static final Logger logger = LoggerFactory.getLogger(FastdfsController.class);
//
//    @Reference(loadbalance = "random",check = false,timeout = 3000) //dubbo直连
//    private fileSystemService fileSystemService;
//
//    @RequestMapping(path = "/shareFilesSetting/back/{path}")
//    public String getNextTopPage(@PathVariable(value = "path") String path) {
//        if (!path.equals("index")) {
//
//            //dir1*dir2
//            int endIndex = path.lastIndexOf('*');
//            //dir1
//            if (endIndex == -1)
//                return "redirect:/shareFiles/show/index";
//
//            return "redirect:/shareFiles/show/" + path.substring(0, endIndex);
//        }
//        return "redirect:/shareFiles/show/index";
//    }
//
//
//    @RequestMapping(path = "/shareFilesSetting/mkdir")
//    public String mkdir(@RequestParam(value = "absolute_path") String absolute_path,
//                        @RequestParam(value = "dirname") String dirname
//            , Page page, Model model) {
//        if(dirname.equals("")){
//            return "redirect:/shareFiles/show/index";
//        }
//        // 分页信息
//        page.setLimit(14);
//        //  absolute_path:   dir2*file  absolute_path:   index
//        String realPath = absolute_path.replace("*", "/");
//
//        if (realPath.equals("index")) {
//            page.setPath("/shareFiles/show/index");
//            fileSystemService.insertFileSystem(dirname, absolute_path + "*" + dirname);
//
//        } else {
//            page.setPath("/shareFiles/show" + page.getPath());
//            fileSystemService.insertFileSystem(realPath + "/" + dirname, absolute_path + "*" + dirname);
//        }
//
//
//        int size = fileSystemService.fileSize(realPath);
//        page.setRows(size);
//
//        // files = {"dir1*dir2*dir3"}
//        List<File> files = fileSystemService.list(page.getCurrent(), page.getLimit(), realPath);
//
//        model.addAttribute("files", files);
//        model.addAttribute("absolute_path", absolute_path);
//        return "site/shareFiles";
//    }
//
//
//    // /shareFiles/index   /shareFiles/dir1*dir2  /shareFiles/dir2*file
//    @RequestMapping(path = "/shareFiles/show/{absolute_path}")
//    public String getPage(@PathVariable("absolute_path") String absolute_path,
//                          Model model, Page page) {
//        // path:  index   dir1*dir2 file
//        String realPath = absolute_path.replace("*", "/");
//        String[] strings = realPath.split("/");
//        String fileName = strings[strings.length - 1];
//        //表明是文件
//        if (!absolute_path.equals("index") && (fileName.split(".").length != 0))
//            //path: file
//            return "redirect:/shareFiles/file/" + absolute_path;
//
//        // index dir1/dir2
//        int size = fileSystemService.fileSize(realPath);
//        page.setRows(size);
//
//        // 分页信息
//        page.setLimit(14);
//        // /shareFiles/index  /shareFiles/dir1*dir2
//        page.setPath("/shareFiles/show/" + absolute_path);
//
//        List<File> files = fileSystemService.list(page.getCurrent(), page.getLimit(), realPath);
//
//        model.addAttribute("files", files);
//        model.addAttribute("absolute_path", absolute_path);
//        return "site/shareFiles";
//    }
//
//
//    @LoginRequired
//    @RequestMapping(path = "/files/upload", method = RequestMethod.POST)
//    public String uploadFile(@RequestParam(value = "file") MultipartFile url,
//                             @RequestParam(value = "absolute_path") String absolute_path,
//                             Model model, Page page) {
//        // path: ""   dir1*dir2
//        if (url == null) {
//            model.addAttribute("error", "您还没有选择文件!");
//            return "site/shareFiles";
//        }
//        String fileName = url.getOriginalFilename();
//        String suffix = fileName.substring(fileName.lastIndexOf("."));
//        if (StringUtils.isBlank(suffix)) {
//            model.addAttribute("error", "文件的格式不正确!");
//            return "site/shareFiles";
//        }
//
//        final String fastdfsName = fileSystemService.uploadFile(MultipartSerializable.file2json(url));
//
//        String absolutePath = null;
//        if (absolute_path.equals("index")) {
//            absolutePath = fileName;
//
//        } else {
//            absolutePath = absolute_path.replace("*", "/") + "/" + fileName;
//        }
//
//        // 分页信息
//        page.setLimit(14);
//        if (page.getPath() == null) {
//            page.setPath("/shareFiles/show/index");
//        } else {
//            page.setPath("/shareFiles/show/" + page.getPath());
//        }
//
//        fileSystemService.insertFileSystem(absolutePath, fastdfsName);
//
//
//        int size = fileSystemService.fileSize(absolute_path.replace("*", "/"));
//        page.setRows(size);
//
//        // files = {"dir1*dir2*dir3"}
//        List<File> files = fileSystemService.list(page.getCurrent(), page.getLimit(), absolute_path.replace("*", "/"));
//
//        model.addAttribute("files", files);
//        model.addAttribute("absolute_path", absolute_path);
//        return "redirect:"+page.getPath();
//
//    }
//
//
//    // shareFiles/show/file
//    @RequestMapping(path = "/shareFiles/file/{absolute_path}", method = RequestMethod.GET)
//    public String getFile(@PathVariable("absolute_path") String absolute_path,
//                          @RequestParam("fastdfs_name") String fastdfs_name,
//                          HttpServletResponse response, Model model) {
//
//
//
//        //path:  file
//        Map<String, String> supMDLang = new HashMap<>();
//        supMDLang.put("cpp", "cpp");
//        supMDLang.put("java", "java");
//        supMDLang.put("html", "html");
//        supMDLang.put("py", "python");
//        supMDLang.put("md", "");
//
//
//        final String suffix = absolute_path.substring(absolute_path.lastIndexOf(".") + 1);
//        if (supMDLang.containsKey(suffix)) {
//            StringBuilder code = new StringBuilder();
//            try (
//                    InputStream is = new URL(fastdfs_name).openStream();
//                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
//            ) {
//                String buffer = null;
//                while ((buffer = br.readLine()) != null) {
//                    buffer += '\n';
//                    code.append(buffer);
//                }
//            } catch (Exception e) {
//                logger.error("读取java文件失败: " + e.getMessage());
//            }
//
//            // java 启动在线编译器
//            if (suffix.equals("java")) {
//                model.addAttribute("javaFileContent", code);
//                return "site/onlineExecutor";
//            } else if (suffix.equals("md")) {
//                model.addAttribute("code", code);
//                return "site/show-code";
//            } else {
//                String language = supMDLang.get(suffix);
//                StringBuilder suffixAndCode = new StringBuilder();
//                suffixAndCode.append("```").append(language).append("\n").append(code).append("```");
//                // 其他语言 启动mardown显示
//                model.addAttribute("code", suffixAndCode);
//                return "site/show-code";
//            }
//
//        } else {
//
//            // 响应文件
//            response.setContentType("application/x-download");
//            try (
//                    InputStream is = new URL(fastdfs_name).openStream();
//                    OutputStream os = response.getOutputStream();
//            ) {
//                byte[] buffer = new byte[1024];
//                int b = 0;
//                while ((b = is.read(buffer)) != -1) {
//                    os.write(buffer, 0, b);
//                }
//            } catch (IOException e) {
//                logger.error("读取其他文件失败: " + e.getMessage());
//            }
//        }
//        return "site/shareFiles";
//    }
//
//
//    @RequestMapping(path = "/shareFilesSetting/delete", method = RequestMethod.GET)
//    public String deleteFileAndDir(@RequestParam("absolute_path") String absolute_path) {
//
//        if(!absolute_path.equals("")){
//            int result = fileSystemService.deleteFileSystem(absolute_path);
//        }
//        return "redirect:/shareFiles/show/index";
//    }
//
//    @RequestMapping(path = "/shareFilesSetting/modify", method = RequestMethod.GET)
//    public String modifyFileAndDir(@RequestParam("absolute_path") String absolute_path,
//                                   @RequestParam("modified_path") String modified_path) {
//        if(!absolute_path.equals("") && !modified_path.equals("")) {
//            int result = fileSystemService.modifyFileSystem(absolute_path, modified_path);
//        }
//        return "redirect:/shareFiles/show/index";
//    }
//
//    @RequestMapping(path = "/shareFilesSetting/clean", method = RequestMethod.GET)
//    public String cleanDeletedFiles() {
//        List<File> files = fileSystemService.selectDeletedFiles();
//
//        files.stream().forEach(f -> {
//            fileSystemService.cleanFileSystem(f.getFastdfsName());
//        });
//        return "redirect:/shareFiles/show/index";
//    }
//}
