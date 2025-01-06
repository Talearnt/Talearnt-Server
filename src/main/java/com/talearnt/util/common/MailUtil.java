package com.talearnt.util.common;

public class MailUtil {
    
    /** FE 서버 등록 시 그 주소로 포트와 함께 변경,
     * 로고 S3에 등록시 변경
     * 고객센터 주소 생길 시 변경
     * 나중에는 폰트와 크기 색상 변경 필요
     * */
    public static String MakeFindPasswrodMailForm(Long findPasswordUrlNo, String uuid){
        //로고, 고객센터 주소 추가해야함.
        String html = "<table role=\"presentation\" style=\"width: 100%; max-width: 628px; margin: 0 auto; border-spacing: 0; font-family: Arial, sans-serif;\">\n" +
                "    <!-- Header -->\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 20px; border-bottom: 1px solid #d0d5d8;\">\n" +
                "            <table role=\"presentation\" style=\"width: 100%; border-spacing: 0;\">\n" +
                "                <tr>\n" +
                "                    <td>\n" +
                "                        <img src=\"https://talearnt-sever-images-upload-bucket.s3.ap-northeast-2.amazonaws.com/talearnt_logo.png\" alt=\"로고\" style=\"max-height: 50px; display: block;\">\n" +
                "                    </td>\n" +
                "                    <td style=\"text-align: right;\">\n" +
                "                        <a href=\"#\" style=\"font-weight: 600; font-size: 20px; color: #000; text-decoration: none;\">고객 센터</a>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "\n" +
                "    <!-- Title Section -->\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 20px; text-align: center;\">\n" +
                "            <p style=\"font-size: 20px; font-weight: 600; margin: 0;\">비밀번호 재설정 인증 안내</p>\n" +
                "            <p style=\"font-size: 17px; font-weight: 500; margin: 10px 0;\">비밀번호 재설정을 위한 인증 이메일입니다.</p>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "\n" +
                "    <!-- Message Box -->\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 20px;\">\n" +
                "            <table role=\"presentation\" style=\"width: 100%; border-spacing: 0; border: 1px solid #d0d5d8; background-color: #f7f8f8; border-radius: 10px;\">\n" +
                "                <tr>\n" +
                "                    <td style=\"padding: 20px; text-align: center;\">\n" +
                "                        <p style=\"font-size: 15px; font-weight: 600; margin: 0;\">안녕하세요. talearnt입니다.</p>\n" +
                "                        <p style=\"margin: 10px 0;\">요청하신 <span style=\"color: #1b76ff;\">비밀번호 재설정</span>을 위한 본인확인 메일입니다.</p>\n" +
                "                        <p style=\"margin: 0;\">비밀번호 재설정을 하시려면 아래의 버튼을 클릭해 주세요.</p>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "\n" +
                "    <!-- Button -->\n" +
                "    <tr>\n" +
                "        <td style=\"padding: 20px; text-align: center;\">\n" +
                "            <table role=\"presentation\" style=\"width: 100%; margin: 0 auto; border-spacing: 0;\">\n" +
                "                <tr>\n" +
                "                    <td style=\"background-color: #1b76ff; border-radius: 10px; text-align: center;\">\n" +
                "                        <a href=\"http://localhost:5173/find-account/change?no="+findPasswordUrlNo+"&uuid="+uuid+"\"\n" +
                "                           style=\"display: inline-block; padding: 15px; color: #fff; background-color: #1b76ff; text-decoration: none; border-radius: 10px; font-size: 16px; text-align: center; font-weight: bold;\">\n" +
                "                            비밀번호 재설정하러 가기\n" +
                "                        </a>\n" +
                "                    </td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "        </td>\n" +
                "    </tr>\n" +
                "</table>";
        return html;
    }
}
