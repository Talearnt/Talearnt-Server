package com.talearnt.util.common;

public class MailUtil {
    
    /** FE 서버 등록 시 그 주소로 포트와 함께 변경,
     * 로고 S3에 등록시 변경
     * 고객센터 주소 생길 시 변경
     * 나중에는 폰트와 크기 색상 변경 필요
     * */
    public static String MakeFindPasswrodMailForm(Long findPasswordUrlNo, String uuid){
        String html = "<div style='padding: 0px 20px; display: flex; flex-direction: column; justify-content: space-between; align-items: center; width: 628px; height: 504px; '>\n" +
                "    <div style='display: flex; align-items: center; justify-content: space-between; width: 100%; border-bottom: 1px solid #d0d5d8;'>\n" +
                "        <img src='로고 주소'>\n" +
                "        <a href='#' style='font-weight: 600; font-size: 20px; color:#000; text-decoration: none;'>고객 센터</a>\n" +
                "    </div>\n" +
                "    <div style='display: flex; flex-direction: column; align-items: center; justify-content: center;'>\n" +
                "        <p style='font-size: 20px; font-weight: 600; margin:0px;'>비밀번호 재설정 인증 안내</h4>\n" +
                "        <p style='font-size: 17px; font-weight: 550; margin:0px;'>비밀번호 재설정을 위한 인증 이메일입니다.</p>\n" +
                "    </div>\n" +
                "    <div style='width: 100%;display: flex; flex-direction: column; align-items: center; justify-content: center; border: 1px solid #d0d5d8; background-color: #f7f8f8; width: 100%; height: 129px; border-radius: 10px; box-shadow: #d0d5d8 1px 3px 1px;'>\n" +
                "        <p style='font-size: 15px; font-weight: 600; padding-bottom: 5px; margin: 0px;'>안녕하세요. talearnt입니다.</p>\n" +
                "        <p style='margin: 0px;'>요청하신 <span style='color: #1b76ff; margin: 0px;'>비밀번호 재설정</span>을 위한 본인확인 메일입니다.</p>\n" +
                "        <p style='margin: 0px;'><span style='color: #1b76ff; margin: 0px;'>비밀번호 재설정</span>을 하시려면 아래의 버튼을 클릭해 주세요.</p>\n" +
                "    </div>\n" +
                "   <div style='border:#1b76ff; width:580px; padding: 20px 20px; display: flex; align-items: center; justify-content: center; color: #fff; background-color: #1b76ff; text-decoration: none; border-radius: 10px;'>\n" +
                "        <a href='http://localhost:5173/"+findPasswordUrlNo+"/password/"+uuid+"' style='color: #fff;text-decoration: none;'>비밀번호 재설정하러 가기</a>\n" +
                "    </div>" +
                "</div>";
        return html;
    }
}
