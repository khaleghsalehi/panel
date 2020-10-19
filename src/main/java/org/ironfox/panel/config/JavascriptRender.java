package org.ironfox.panel.config;

public class JavascriptRender {
    //todo all of script MUST be obfuscate before deployment in productive mode.
    //todo all of javascript file names MUST be rename before deployment in productive mode.

    public final static String RENDER_BUFFER_SIZE = "4096k";
    public final static String HEADER_RENDER = "<script src='/fingerprint.js'></script>\n" +
            "    <script src='/cryptojs.js'></script>\n</head>";
    public final static String BODY_RENDER = "<script>\n" +
            "    function ReverseString(str) {\n" +
            "        if (!str || str.length < 2 ||\n" +
            "            typeof str !== 'string') {\n" +
            "            return 'Not valid';\n" +
            "        }\n" +
            "        const revArray = [];\n" +
            "        const length = str.length - 1;\n" +
            "        for (let i = length; i >= 0; i--) {\n" +
            "            revArray.push(str[i]);\n" +
            "        }\n" +
            "        return revArray.join('');\n" +
            "    }\n" +
            "\n" +
            "    function makeid(length) {\n" +
            "        var result = '';\n" +
            "        var characters = 'ABCDEF123456789';\n" +
            "        var charactersLength = characters.length;\n" +
            "        for (var i = 0; i < length; i++) {\n" +
            "            result += characters.charAt(Math.floor(Math.random() * charactersLength));\n" +
            "        }\n" +
            "        return result;\n" +
            "    }\n" +
            "\n" +
            "    function ascii_to_hexa(str) {\n" +
            "        var arr1 = [];\n" +
            "        for (var n = 0, l = str.length; n < l; n++) {\n" +
            "            var hex = Number(str.charCodeAt(n)).toString(16);\n" +
            "            arr1.push(hex);\n" +
            "        }\n" +
            "        return arr1.join('');\n" +
            "    }\n" +
            "\n" +
            "    function aesEncrypt(data, key, iv) {\n" +
            "        let cipher = CryptoJS.AES.encrypt(data, CryptoJS.enc.Utf8.parse(key), {\n" +
            "            iv: CryptoJS.enc.Utf8.parse(iv),\n" +
            "            padding: CryptoJS.pad.Pkcs7,\n" +
            "            mode: CryptoJS.mode.CBC\n" +
            "        });\n" +
            "        return cipher.toString();\n" +
            "    }\n" +
            "\n" +
            "    function base64ToHex(str) {\n" +
            "        const raw = atob(str);\n" +
            "        let result = '';\n" +
            "        for (let i = 0; i < raw.length; i++) {\n" +
            "            const hex = raw.charCodeAt(i).toString(16);\n" +
            "            if (hex == '0') {\n" +
            "                console.log(result + '->[null at index ' + i * 2 + ']');\n" +
            "                return '-1'\n" +
            "            }\n" +
            "            result += (hex.length === 2 ? hex : '0' + hex);\n" +
            "        }\n" +
            "        return result.toUpperCase();\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    function gethash() {\n" +
            "\n" +
            "        Fingerprint2.get(function (components) {\n" +
            "                murmur = Fingerprint2.x64hash128(components.map(function (pair) {\n" +
            "                    return pair.value\n" +
            "                }).join(), 31)\n" +
            "\n" +
            "                var counter = 0\n" +
            "                var payload = '-1';\n" +
            "                while (payload === '-1') {\n" +
            "                    counter++\n" +
            "                    let key = makeid(32);\n" +
            "                    let iv = makeid(16);\n" +
            "                    payload = base64ToHex(aesEncrypt(murmur, key, iv));\n" +
            "                    document.cookie = 'token=' + key + iv + payload + Date.now().toString() + '; expires=Thu, 31-Dec-25 23:55:55 GMT; path=/';\n" +
            "                    console.log(key);\n" +
            "                    console.log(iv);\n" +
            "                    console.log(payload);\n" +
            "                    console.log('conter = > ' + counter)\n" +
            "                }\n" +
            "            }\n" +
            "        )\n" +
            "\n" +
            "    }\n" +
            "    gethash()\n" +
            "</script>\n" +
            "\n" +
            "</body>";
}
