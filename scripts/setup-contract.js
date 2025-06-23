const fs = require("fs");
const {
  getBALNContract,
  setBALNContract,
  getBNUSDContract,
  setBNUSDContract,
  sleep,
  getTxResult,
} = require("../utils/utils");
const config = require("../utils/config");

async function main() {
  try {
    console.log("Setting BALN contract...");
    const tx = await setBALNContract();
    sleep(5000);
    const txResult = await getTxResult(tx);

    if (txResult.status !== 1) {
      console.log(txResult);
      throw new Error("Error setting BALN contract");
    }

    const balnContract = await getBALNContract();

    console.log("BALN contract: ", balnContract);
    if (balnContract !== config.default.token.BALN) {
      throw new Error("Error setting BALN contract");
    }

    console.log("BALN contract set successfully");

    console.log("Setting BNUSD contract...");
    const bnusdTx = await setBNUSDContract();
    sleep(5000);
    const bnusdTxResult = await getTxResult(bnusdTx);

    if (bnusdTxResult.status !== 1) {
      console.log(bnusdTxResult);
      throw new Error("Error setting BNUSD contract");
    }

    const bnusdContract = await getBNUSDContract();

    console.log("BNUSD contract: ", bnusdContract);
    if (bnusdContract !== config.default.token.BNUSD) {
      throw new Error("Error setting BNUSD contract");
    }

    console.log("BNUSD contract set successfully");
  } catch (err) {
    console.log("Error running setup-contract.js");
    console.log(err);
  }
}

main();
