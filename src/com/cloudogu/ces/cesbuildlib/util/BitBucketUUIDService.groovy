package com.cloudogu.ces.cesbuildlib.util
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import net.sf.json.JSON

class BitBucketUUIDService {
    static String repoPath = 'https://api.bitbucket.org/2.0/repositories/$ownerSlug/$repoSlug'

    public static def getRepoInfo(String user, String secret, String repoSlug, String ownerSlug) {
        JsonSlurper slurper = new JsonSlurper()
        def token = getToken(user, secret)
        def repoURL = new SimpleTemplateEngine().createTemplate(repoPath).make([ownerSlug: ownerSlug, repoSlug: repoSlug])
        def result = new RESTClient(repoURL).get(contentType: ContentType.JSON, query: [access_token: token])
        return [ repo: result['data'].uuid, owner: result['data'].owner.uuid ]
    }

    private static def getToken(String user, String secret) {
        String  oauthPath = "https://bitbucket.org"
        def http = new HTTPBuilder(oauthPath, ContentType.JSON)
        //http.auth.basic(user, secret)
        def encodedAuth = (user + ":" + secret).bytes.encodeBase64().toString()
        //http.setHeaders([Authorization: "Basic " + encodedAuth])
        http.setHeaders([
                Authorization: "Basic " + encodedAuth,
                Accept: 'text/plain',
                'Content-Type': 'application/x-www-form-urlencoded'])
        def result = http.post(path: "/site/oauth2/access_token",

                body: ["grant_type":"client_credentials"])

        return result['access_token']

    }
}
