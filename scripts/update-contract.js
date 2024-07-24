const fs = require("fs");
const { deployContract, sleep, getTxResult } = require("../utils/utils");
const config = require("../utils/config");

async function main() {
  try {
    const content =
      "0x" +
      fs
        .readFileSync(
          "./smart-contract/contract/build/libs/contract-0.1.0-optimized.jar",
        )
        .toString("hex");

    const tx = await deployContract(content, config.contract.address);
    console.log("tx:");
    console.log(tx);
    await sleep(4000);
    const txResult = await getTxResult(tx);
    console.log("txResult:");
    console.log(txResult);
  } catch (err) {
    console.log("Error deploying contract");
    console.log(err);
  }
}

main();
