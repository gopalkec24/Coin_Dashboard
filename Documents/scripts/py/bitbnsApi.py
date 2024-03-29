import requests
import json, time, base64, hmac, hashlib

class bitbnsApi():
    apiKeys = dict()
    baseUrl = 'https://api.bitbns.com/api/trade/v1'

    def __init__(self, apiKey, apiSecretKey):
        self.apiKeys['apiKey'] = apiKey
        self.apiKeys['apiSecretKey'] = apiSecretKey

    def genErrorMessage(self, data, status, error):
        ret = dict()
        ret['data'] = data
        ret['status'] = status
        ret['error'] = error
        return ret

    def requestAuthenticate(self, symbol):
        if isinstance(symbol,str) and len(symbol) >= 1:
            return True
        else:
            return False

    def verifyApiKeys(self, data):
        if isinstance(data['apiKey'],str) and isinstance(data['apiSecretKey'],str) and len(data['apiKey']) >= 5 and len(data['apiSecretKey']):
            return True
        else:
            return False

    def initHeaders(self):
        api_headers = dict()
        api_headers['X-BITBNS-APIKEY'] = ''
        api_headers['X-BITBNS-PAYLOAD'] = ''
        api_headers['X-BITBNS-SIGNATURE'] = ''
        api_headers['Accept'] = 'application/json'
        api_headers['Accept-Charset'] = 'utf-8'
        api_headers['content-type'] = 'application/x-www-form-urlencoded'
        return api_headers

    def getPayload(self, symbol, body):
        timeStamp_nonce = str(int(time.time()*1000.0))
        data = dict()
        data['symbol'] = symbol
        data['timeStamp_nonce'] = str(timeStamp_nonce)
        data['body'] = body
        data = json.dumps(data)
        data = data.replace(" ","")
        encoded = base64.b64encode(data.encode())
        return encoded.decode()

    def getSignature(self, payload, apiSecretKey):
        m = hmac.new(apiSecretKey.encode('utf-8'), payload.encode('utf-8'), hashlib.sha512)
        return m.hexdigest()

    def populateHeadersForPost(self, symbol, methodName, body):
        headers = self.initHeaders()
        payload = self.getPayload('/'+methodName+'/'+symbol,body)
        signature = self.getSignature(payload, self.apiKeys['apiSecretKey'])
        headers['X-BITBNS-APIKEY'] = self.apiKeys['apiKey'];
        headers['X-BITBNS-PAYLOAD'] = payload;
        headers['X-BITBNS-SIGNATURE'] = signature;
        return headers

    def makePostRequest(self, symbol, methodName, body):
        options = dict()
        options['url'] = self.baseUrl + '/' + methodName + '/' + symbol
        options['body'] = json.dumps(body)
        options['body'] = options['body'].replace(" ","")
        headers = self.populateHeadersForPost(symbol, methodName, json.dumps(body))
        options['headers'] = headers
        try:
            req = requests.post(options['url'],headers=options['headers'],data=options['body'])
            return req.json()
        except:
            return self.genErrorMessage(None, 0, 'error while making post request')

    def listOpenStopOrders(self, symbol):
        body = dict()
        body['page'] = 0
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'listOpenStopOrders', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def listOpenOrders(self, symbol):
        body = dict()
        body['page'] = 0
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'listOpenOrders', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def currentCoinBalance(self, symbol):
        body = dict()
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'currentCoinBalance', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def placeBuyOrder(self, symbol, quantity, rate):
        body = dict()
        body['rate'] = rate
        body['quantity'] = quantity
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'placeBuyOrder', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def depositHistory(self, symbol, page):
        body = dict()
        body['page'] = page
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'depositHistory', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def buyStopLoss(self, symbol, quantity, rate, t_rate):
        body = dict()
        body['quantity'] = quantity
        body['rate'] = rate
        body['t_rate'] = t_rate
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'buyStopLoss', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def sellStopLoss(self, symbol, quantity, rate, t_rate):
        body = dict()
        body['quantity'] = quantity
        body['rate'] = rate
        body['t_rate'] = t_rate
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'sellStopLoss', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def getCoinAddress(self, symbol):
        body = dict()
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'getCoinAddress', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def getApiUsageStatus(self):
        body = dict()
        if self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest('USAGE', 'getApiUsageStatus', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def withdrawHistory(self, symbol, page):
        body = dict()
        body['page'] = page
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'withdrawHistory', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def placeSellOrder(self, symbol, quantity, rate):
        body = dict()
        body['quantity'] = quantity
        body['rate'] = rate
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'placeSellOrder', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def cancelOrder(self, symbol, entry_id):
        body = dict()
        body['entry_id'] = entry_id
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'cancelOrder', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def orderStatus(self, symbol, entry_id):
        body = dict()
        body['entry_id'] = entry_id
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'orderStatus', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def cancelStopLossOrder(self, symbol, entry_id):
        body = dict()
        body['entry_id'] = entry_id
        if self.requestAuthenticate(symbol) and self.verifyApiKeys(self.apiKeys):
            return self.makePostRequest(symbol, 'cancelStopLossOrder', body)
        else:
            return self.genErrorMessage(None,0,'please recheck the parameters')

    def platformStatus(self):
        try:
            req = requests.get(self.baseUrl+'/platform/status',headers={"X-BITBNS-APIKEY":self.apiKeys['apiKey']})
            return req.json()
        except:
            return self.genErrorMessage(None,0,'some error in get req')

    def getBuyOrderBook(self, symbol):
        try:
            req = requests.get(self.baseUrl+'/orderbook/buy/{}'.format(symbol),headers={"X-BITBNS-APIKEY":self.apiKeys['apiKey']})
            return req.json()
        except:
            return self.genErrorMessage(None,0,'some error in get req')

    def getSellOrderBook(self, symbol):
        try:
            req = requests.get(self.baseUrl+'/orderbook/sell/{}'.format(symbol),headers={"X-BITBNS-APIKEY":self.apiKeys['apiKey']})
            return req.json()
        except:
            return self.genErrorMessage(None,0,'some error in get req')

    def getTickerApi(self, symbols):
        allSymbol = symbols.split(',')
        try:
            req = requests.get(self.baseUrl+'/tickers',headers={"X-BITBNS-APIKEY":self.apiKeys['apiKey']})
            req = req.json()
        except:
            return self.genErrorMessage(None,0,'some error in get req')
        for key, item in req.items():
            req[key].pop('yes_price', None)
            req[key].pop('volume', None)
        if len(allSymbol) == 1 and allSymbol[0] == '':
            return req
        finallist = dict()
        for item in allSymbol:
            if item not in req:
                return self.genErrorMessage(None,0,'provide proper symbol')
            finallist[item] = req[item]
        return finallist