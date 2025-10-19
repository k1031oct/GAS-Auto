/**
 * @file ExecutionService.gs
 * @description JSONに定義されたロジックを安全に解釈・実行するインタープリタ。
 * このサービスがアプリケーションの「CPU」として機能します。
 */
var ExecutionService = {
  /**
   * JSONから渡されたロジック（ステップの配列）を順次実行する
   * @param {Array<object>} logicSteps - 実行するステップの配列
   * @param {object} initialContext - 初期コンテキスト（UIからの設定値など）
   * @returns {*} 最後のステップの実行結果
   */
  execute: function(logicSteps, initialContext) {
    // 実行中の状態を保持するコンテキストオブジェクト
    const context = {
      ...initialContext, // UIから渡された設定値を初期値とする
      outputs: {} // 各ステップの結果を格納する場所
    };
    let lastResult = null;

    for (const step of logicSteps) {
      if (!step.func) {
        throw new Error(`Execution Error: Step ${step.step_id} is missing the 'func' property.`);
      }
      
      const funcToRun = FunctionRegistry.get(step.func);
      if (!funcToRun) {
        throw new Error(`Security Error: Function "${step.func}" is not registered or not found.`);
      }

      // 引数内のプレースホルダ（例: {{outputs.fileToMove.id}}）を解決する
      const resolvedArgs = this._resolvePlaceholders(step.args || {}, context);

      lastResult = funcToRun(resolvedArgs, context);

      // 'resultTo' キーがあれば、結果をコンテキストに保存
      if (step.resultTo) {
        context.outputs[step.resultTo] = lastResult;
      }
    }

    return lastResult;
  },

  /**
   * 引数オブジェクト内のプレースホルダをコンテキストの値で置き換える
   * @private
   * @param {object} args - プレースホルダを含む引数オブジェクト
   * @param {object} context - 値の解決に使うコンテキストオブジェクト
   * @returns {object} プレースホルダが解決された引数オブジェクト
   */
  _resolvePlaceholders: function(args, context) {
    const resolvedArgs = {};
    for (const key in args) {
      let value = args[key];
      if (typeof value === 'string') {
        // 正規表現で {{...}} 形式のプレースホルダを検索
        const match = value.match(/^{{(.+)}}$/);
        if (match) {
          const path = match[1].trim(); // "outputs.step1.id"
          // パスをドットで分割して、コンテキストを深く探索
          const pathParts = path.split('.');
          let resolvedValue = context;
          for (const part of pathParts) {
            resolvedValue = resolvedValue ? resolvedValue[part] : undefined;
          }
          value = resolvedValue;
        }
      }
      resolvedArgs[key] = value;
    }
    return resolvedArgs;
  }
};
