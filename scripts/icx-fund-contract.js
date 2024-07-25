const { sendIcx, sleep, getTxResult } = require("../utils/utils");
const config = require("../utils/config");
const { contract } = config;

/* EDIT THESE VALUES */
const FUND_AMOUNT = 10;
/* END EDIT */

async function main() {
  try {
    const txHash = await sendIcx(contract.address, FUND_AMOUNT);
    console.log("Transaction hash:", txHash);
    await sleep(1000);
    const txResult = await getTxResult(txHash);
    console.log("Transaction result:");
    console.log(txResult);
  } catch (err) {
    console.log("Error running icx-fund-contract script:");
    console.log(err);
  }
}

main();
