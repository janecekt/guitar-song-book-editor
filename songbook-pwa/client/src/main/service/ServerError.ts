export class ServerError extends Error {
   code : number;
   type : string;

   constructor(code: number, type: string, message: string) {
       super(message);
       this.code = code;
       this.type = type;
   }
}