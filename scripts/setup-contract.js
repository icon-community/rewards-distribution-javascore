const fs = require("fs");
const {
  getBALNContract,
  setBALNContract,
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
  } catch (err) {
    console.log("Error running setup-contract.js");
    console.log(err);
  }
}

main();
