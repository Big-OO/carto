import { serve } from "https://deno.land/std/http/server.ts";

serve(async (req) => {
  try {
    const secret = Deno.env.get("PAYMOB_SECRET_KEY");

    if (!secret) {
      return new Response(
        JSON.stringify({
          error: "PAYMOB_SECRET_KEY is missing",
        }),
        {
          status: 500,
          headers: { "Content-Type": "application/json" },
        }
      );
    }

    if (req.method === "GET" || req.method === "HEAD") {
      const url = new URL(req.url);
      const clientSecret = url.searchParams.get("client_secret");
      if (!clientSecret) {
        return new Response(
          JSON.stringify({ error: "client_secret query parameter is required" }),
          { status: 400, headers: { "Content-Type": "application/json" } }
        );
      }

      return new Response(
        JSON.stringify({
          client_secret: clientSecret,
          status: "confirmed",
          confirmed: true,
        }),
        {
          status: 200,
          headers: { "Content-Type": "application/json" },
        }
      );
    }

    const body = await req.json();

    const response = await fetch(
      "https://accept.paymob.com/v1/intention/",
      {
        method: "POST",
        headers: {
          Authorization: `Token ${secret}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
      }
    );

    const text = await response.text();

    return new Response(text, {
      status: response.status,
      headers: {
        "Content-Type": "application/json",
      },
    });
  } catch (e) {
    return new Response(
      JSON.stringify({
        error: String(e),
      }),
      {
        status: 500,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
  }
});