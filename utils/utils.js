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
  contract = null,
  params = null,
  wallet = WALLET,
  // sl = 1000000000,
  sl = 13000000000,
) {
  try {
    if (contract != null && config.contract.address == null) {
      throw new Error("Contract address is not set in config");
    }
    const txObj = new IconBuilder.DeployTransactionBuilder()
      .from(wallet.getAddress())
      .to(
        contract == null
          ? "cx0000000000000000000000000000000000000000"
          : contract,
      )
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

async function sendIcx(to, amount, wallet = WALLET) {
  try {
    const txObj = new IconBuilder.IcxTransactionBuilder()
      .from(wallet.getAddress())
      .to(to)
      .value(IconConverter.toHex(amount * 10 ** 18))
      .stepLimit(IconConverter.toHex(1000000))
      .nid(config.default.nid)
      .nonce(IconConverter.toHex(1))
      .version(IconConverter.toHex(3))
      .timestamp(new Date().getTime() * 1000)
      .build();

    const signedTransaction = new SignedTransaction(txObj, wallet);
    const txHash = await iconService
      .sendTransaction(signedTransaction)
      .execute();
    return txHash;
  } catch (err) {
    const str = "Error sending ICX";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function addClaim(claim, wallet = WALLET) {
  try {
    const { address, amount } = claim;
    const parsedAmount = Number(amount) * 10 ** 18;
    const txObj = new IconBuilder.CallTransactionBuilder()
      .from(wallet.getAddress())
      .to(config.contract.address)
      .stepLimit(IconConverter.toHex(10000000000))
      .nid(config.default.nid)
      .nonce(IconConverter.toHex(1))
      .version(IconConverter.toHex(3))
      .timestamp(new Date().getTime() * 1000)
      .method("addClaim")
      .params({
        user: address,
        amount: IconConverter.toHex(parsedAmount),
      })
      .build();

    const signedTransaction = new SignedTransaction(txObj, wallet);
    const txHash = await iconService
      .sendTransaction(signedTransaction)
      .execute();
    return txHash;
  } catch (err) {
    const str = "Error adding claims";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

module.exports = {
  deployContract,
  sleep,
  getTxResult,
  sendIcx,
  addClaim,
};
