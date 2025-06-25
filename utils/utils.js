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
  let txResult = null;
  while (loop < maxLoops) {
    try {
      txResult = await iconService.getTransactionResult(txHash).execute();
      return txResult;
    } catch (err) {
      void err;
      console.log(`Mining tx.. (pass ${loop + 1})`);
      loop++;
      await sleep(1000);
      if (loop == maxLoops) {
        console.log("Transaction failed");
        console.log(err);
        console.log(txResult);
        throw new Error("Transaction failed");
      }
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

async function callTransactionMethod(
  to,
  method,
  params,
  sl = 10000000000,
  wallet = WALLET,
) {
  try {
    const txObj = new IconBuilder.CallTransactionBuilder()
      .from(wallet.getAddress())
      .to(to)
      .stepLimit(IconConverter.toHex(sl))
      .nid(config.default.nid)
      .nonce(IconConverter.toHex(1))
      .version(IconConverter.toHex(3))
      .timestamp(new Date().getTime() * 1000)
      .method(method);

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
    const str = "Error calling transaction method";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function sendBALN(amount, to = config.default.token.BALN) {
  try {
    const parsedAmount = Number(amount) * 10 ** 18;
    const method = "transfer";
    const params = {
      _to: config.contract.address,
      _value: IconConverter.toHex(parsedAmount),
    };
    return await callTransactionMethod(to, method, params);
  } catch (err) {
    const str = "Error sending BALN";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function addClaim(claim, method) {
  try {
    const { address, amount } = claim;
    const parsedAmount = Number(amount) * 10 ** 18;
    const to = config.contract.address;
    const params = {
      user: address,
      amount: IconConverter.toHex(parsedAmount),
    };
    return await callTransactionMethod(to, method, params, 100000000);
  } catch (err) {
    const str = "Error adding claims";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function addICXClaim(claim, wallet = WALLET) {
  return addClaim(claim, "addICXClaim", wallet);
}

async function addBALNClaim(claim, wallet = WALLET) {
  return addClaim(claim, "addBALNClaim", wallet);
}

async function setBALNContract() {
  try {
    const to = config.contract.address;
    const params = {
      balnContractAddress: config.default.token.BALN,
    };
    const method = "setBALNContract";
    return await callTransactionMethod(to, method, params);
  } catch (err) {
    const str = "Error calling setBALNContract";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function getBALNContract() {
  try {
    const to = config.contract.address;
    const method = "getBALNContract";
    const call = new IconBuilder.CallBuilder().to(to).method(method).build();
    return await iconService.call(call).execute();
  } catch (err) {
    const str = "Error calling getBALNContract";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function sendBNUSD(amount, to = config.default.token.BNUSD) {
  try {
    const parsedAmount = Number(amount) * 10 ** 18;
    const method = "transfer";
    const params = {
      _to: config.contract.address,
      _value: IconConverter.toHex(parsedAmount),
    };
    return await callTransactionMethod(to, method, params);
  } catch (err) {
    const str = "Error sending BNUSD";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function addBNUSDClaim(claim, wallet = WALLET) {
  return addClaim(claim, "addBNUSDClaim", wallet);
}

async function setBNUSDContract() {
  try {
    const to = config.contract.address;
    const params = {
      bnusdContractAddress: config.default.token.BNUSD,
    };
    const method = "setBNUSDContract";
    return await callTransactionMethod(to, method, params);
  } catch (err) {
    const str = "Error calling setBNUSDContract";
    console.log(str);
    console.log(err);
    throw new Error(str);
  }
}

async function getBNUSDContract() {
  try {
    const to = config.contract.address;
    const method = "getBNUSDContract";
    const call = new IconBuilder.CallBuilder().to(to).method(method).build();
    return await iconService.call(call).execute();
  } catch (err) {
    const str = "Error calling getBNUSDContract";
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
  sendBALN,
  addClaim,
  addICXClaim,
  addBALNClaim,
  setBALNContract,
  getBALNContract,
  sendBNUSD,
  addBNUSDClaim,
  setBNUSDContract,
  getBNUSDContract,
};
