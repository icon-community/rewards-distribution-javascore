const IconService = require("icon-sdk-js");
const config = require("./config");
const {
  IconBuilder,
  HttpProvider,
  IconWallet,
  SignedTransaction,
  IconConverter,
} = IconService.default;

const httpProvider = new HttpProvider(
  "https://" + config.default.url + "/api/v3",
);
const iconService = new IconService.default(httpProvider);
const WALLET = IconWallet.loadPrivateKey(config.wallet.privateKey);

async function deployContract(
  jarContent,
  params = null,
  wallet = WALLET,
  sl = 10000000000,
) {
  try {
    const txObj = new IconBuilder.DeployTransactionBuilder()
      .from(wallet.getAddress())
      .to("cx0000000000000000000000000000000000000000")
      .stepLimit(IconConverter.toHex(sl))
      .nid(config.default.nid)
      .nonce(IconConverter.toHex(1))
      .version(IconConverter.toHex(3))
      .timestamp(new Date().getTime() * 1000)
      .contentType("application/java")
      .content(jarContent);

    if (params != null) {
      txObj.params(params);
    }

    const txObj2 = txObj.build();
    const signedTransaction = new SignedTransaction(txObj2, wallet);
    const txHash = await iconService
      .sendTransaction(signedTransaction)
      .execute();
    return txHash;
  } catch (err) {
    const str = "Error deploying contract";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function getTxResult(txHash) {
  const maxLoops = 10;
  let loop = 0;
  while (loop < maxLoops) {
    try {
      return await iconService.getTransactionResult(txHash).execute();
    } catch (err) {
      void err;
      console.log(`Mining tx.. (pass ${loop + 1})`);
      loop++;
      await sleep(1000);
    }
  }
}

async function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

module.exports = {
  deployContract,
  sleep,
  getTxResult,
};
